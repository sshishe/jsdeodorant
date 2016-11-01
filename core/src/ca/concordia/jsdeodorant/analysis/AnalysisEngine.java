package ca.concordia.jsdeodorant.analysis;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.StatementProcessor;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclarationKind;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.MethodType;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;
import ca.concordia.jsdeodorant.analysis.util.FileUtil;
import ca.concordia.jsdeodorant.analysis.util.JSONReader;
import ca.concordia.jsdeodorant.analysis.util.StringUtil;
import ca.concordia.jsdeodorant.experiment.CSVOutput;
import ca.concordia.jsdeodorant.experiment.ClassAnalysisReport;
import ca.concordia.jsdeodorant.experiment.PostgresOutput;
import ca.concordia.jsdeodorant.metrics.CyclomaticComplexity;

public class AnalysisEngine {
	static Logger log = Logger.getLogger(AnalysisEngine.class.getName());
	private final ExtendedCompiler compiler;
	private final CompilerOptions compilerOptions;
	private ImmutableList<SourceFile> inputs;
	private ImmutableList<SourceFile> externs;
	private PostgresOutput psqlOutput;

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions) {
		this(compiler, compilerOptions, null, null);
	}

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions, ImmutableList<SourceFile> inputs, ImmutableList<SourceFile> externs) {
		this.compiler = compiler;
		this.compilerOptions = compilerOptions;
		this.compilerOptions.setIdeMode(false);
		this.compilerOptions.skipAllCompilerPasses();
		this.compilerOptions.setParseJsDocDocumentation(false);
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(this.compilerOptions);
		this.compilerOptions.setNewTypeInference(false);
		WarningLevel warningLevel = WarningLevel.QUIET;
		warningLevel.setOptionsForWarningLevel(this.compilerOptions);
		this.inputs = inputs;
		this.externs = externs;
	}

	public List<Module> run(AnalysisOptions analysisOption) {
		compiler.compile(externs, inputs, compilerOptions);
		ScriptParser scriptAnalyzer = new ScriptParser(compiler);
		JSproject jsProjectInstance = JSproject.getInstance(true);
		if (analysisOption.isOutputToCSV())
			prepareOutputToCSV();

		if (analysisOption.isOutputToDB())
			prepareOutputToPsql(analysisOption);

		int functionCounts = 0;
		
		analysisOption.notifyAnalysisObservers(AnalysisStep.BUILDING_MODEL);
		for (SourceFile sourceFile : inputs) {
			// if (containsError(sourceFile, result))
			// continue;
			Program program = new Program();

			ProgramTree programTree = scriptAnalyzer.parse(sourceFile);
			for (ParseTree sourceElement : programTree.sourceElements) {
				StatementProcessor.processStatement(sourceElement, program);
			}

			jsProjectInstance.createModule(program, sourceFile, scriptAnalyzer.getMessages(), analysisOption.getPackageSystem(), analysisOption.hasModuleAnalysis());
		}
		
		List<Module> modules= jsProjectInstance.getModules();
		for (Module module : modules) {
			markBuiltinLibraries(module, analysisOption);
		}
		
		InheritanceInferenceEngine inheritanceInferenceEngine = new InheritanceInferenceEngine();
		
		inheritanceInferenceEngine.configure(analysisOption.getPackageSystem());
		
		analysisOption.notifyAnalysisObservers(AnalysisStep.IDENTIFYING_CLASSES);
		for (Module module : modules) {
			if (module.getLibraryType() != LibraryType.BUILT_IN) {
				checkForBeingLibrary(module, analysisOption);
				addBuiltinDepdendencies(module, analysisOption, modules);
			}
			if (analysisOption.hasModuleAnalysis())
				jsProjectInstance.processModules(module, analysisOption.getPackageSystem(), false);

			if (analysisOption.hasClassAnlysis()){
				if (analysisOption.analyzeLibrariesForClasses() && module.getLibraryType() == LibraryType.NONE){
					CompositePostProcessor.processFunctionDeclarationsToFindClasses(module, analysisOption.getClassAnalysisMode());
				}
				inheritanceInferenceEngine.run(module );
			}
			

			if (analysisOption.hasFunctionAnlysis())
				if (module.getLibraryType() == LibraryType.NONE)
					CompositePostProcessor.processFunctionInvocations(module);

			if (analysisOption.isCalculateCyclomatic()) {
				CyclomaticComplexity cyclomaticComplexity = new CyclomaticComplexity(module.getProgram());

				for (Map.Entry<String, Integer> entry : cyclomaticComplexity.calculate().entrySet()) {
					log.warn("Cyclomatic Complexity of " + entry.getKey() + " is: " + entry.getValue());
				}
			}
		}
		
		analysisOption.notifyAnalysisObservers(AnalysisStep.BUILDING_CLASS_HIERARCHIES);
		// the inheritance analysis needs to be finished then we find method and attributes 
		inheritanceInferenceEngine.buildInheritenceRelation(analysisOption.getPackageSystem());
		for (Module module : modules) {
			module.identifyConstructorInTypeDeclarationBody(); // this is when the class contains a constructor too
			if(analysisOption.getClassAnalysisMode() == ClassAnalysisMode.NON_STRICT)
					ClassInferenceEngine.analyzeMethodsAndAttributes(module);
			else{
				for(TypeDeclaration aType: module.getTypes()){
					log.debug("identifying methods and attributes for class/interface: "+aType.getName()+ ", at line: "+  aType.getFunctionDeclaration().getFunctionDeclarationTree().location.start.line+ ", in file: "+  aType.getFunctionDeclaration().getFunctionDeclarationTree().location.start.source.name);
					aType.identifyAttributes();
					aType.identifyMethodsWithinClassBody();
					aType.identifyMethodsAddedToClassPrototype();
				}
			}
		}
		log.debug("DONE identifying methods and attributes for classes/interfaces");
		// when all methods of classes are identified  we will  find abstract and overriden and overriding methods
		log.debug("START identifying abstartc, overriden and overriding methods");
		for (Module module : modules) {
			if(analysisOption.getClassAnalysisMode() == ClassAnalysisMode.STRICT)	{
				for(TypeDeclaration aType: module.getTypes()){
					aType.identifyInheritanceRelatedMethods();
				}
				for(TypeDeclaration aType: module.getTypes()){
					if(module.isInterface(aType)){
						if(aType.getKinds()!=null){
							aType.getKinds().add(TypeDeclarationKind.INTERFACE);
						}else{
							EnumSet<TypeDeclarationKind> kinds=  EnumSet.of(TypeDeclarationKind.INTERFACE);
							aType.setKinds(kinds);
						}
					}else{						
						if(module.isAbstractClass(aType)){
							if(aType.getKinds()!=null){
								aType.getKinds().add(TypeDeclarationKind.ABSTRACT_CLASS);
							}else{
								EnumSet<TypeDeclarationKind> kinds=  EnumSet.of(TypeDeclarationKind.ABSTRACT_CLASS);
								aType.setKinds(kinds);
							}
						}else{ // not abstarct class
							if(aType.getKinds()!=null){
								aType.getKinds().add(TypeDeclarationKind.CLASS);
							}else{
								EnumSet<TypeDeclarationKind> kinds=  EnumSet.of(TypeDeclarationKind.CLASS);
								aType.setKinds(kinds);
							}
						}
					}
				}
			}
		}
		log.debug("DONE identifying abstartc, overriden and overriding methods");
		
		for (Module module : modules) {
			if (analysisOption.isOutputToCSV()) {
				CSVOutput csvOutput = new CSVOutput(module);
				csvOutput.functionSignatures();
				csvOutput.functionInvocations();
				csvOutput.uniqueClassDeclaration();
			}
	
			if (analysisOption.isOutputToDB()) {
				psqlOutput.logModuleInfo(module);
				if (analysisOption.hasClassAnlysis())
					psqlOutput.logClasses(module);
				if (analysisOption.hasFunctionAnlysis())
					psqlOutput.logFunctions(module);
			}
	
			AnalysisResult.addPackageInstance(module);
			functionCounts += module.getProgram().getFunctionDeclarationList().size();
		}

		for (Module module : modules) {
			for (TypeDeclaration classDeclaration : module.getTypes()) {
				int methodCount=0;
				int attrCount=0;
				for(TypeMember member: classDeclaration.getTypeMembers()){	
					if(member instanceof Method){
						methodCount++;
					}else{
						attrCount++;
					}
				}
				log.warn("The class name is: " + classDeclaration.getName() + " / attributes: " + methodCount+ " / methods:" + attrCount + " Is infered: " + classDeclaration.isInfered() + " Instantiation count: " + classDeclaration.getInstantiationCount() + " Has namespace:" + classDeclaration.hasNamespace());
			}
		}
		if (analysisOption.isOutputToCSV()) {
			ClassAnalysisReport.updateReport(modules);
			ClassAnalysisReport.writeToCSV();
			CSVOutput experimentOutput = new CSVOutput();
			experimentOutput.aggregateReportForModule(modules);
			experimentOutput.moduleReport(modules);
			log.info("Total number of classes: " + ClassAnalysisReport.getClassCount());
			log.info("Total number of functions: " + functionCounts);
		}
		//log.info("Total number of files: " + AnalysisResult.getTotalNumberOfFiles());
		return modules;
	}

	private void prepareOutputToCSV() {
		CSVOutput.createAndClearFolder("log/functions");
		CSVOutput.createAndClearFolder("log/legacy/classes");
		CSVOutput.createAndClearFolder("log/classes");
		CSVOutput.createAndClearFolder("log/aggregate");
	}

	private void prepareOutputToPsql(AnalysisOptions analysisOption) {
		String name = "";
		String version = "";
		File packageConfigFile = new File(analysisOption.getDirectoryPath() + "/package.json");
		JSONReader reader = new JSONReader();
		if (!StringUtil.isNullOrEmpty(analysisOption.getName()))
			name = analysisOption.getName();
		if (!StringUtil.isNullOrEmpty(analysisOption.getVersion()))
			version = analysisOption.getVersion();
		if (StringUtil.isNullOrEmpty(name) && StringUtil.isNullOrEmpty(version))
			try {
				name = reader.getElementFromObject(packageConfigFile.getCanonicalPath(), "name");
				version = reader.getElementFromObject(packageConfigFile.getCanonicalPath(), "version");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		psqlOutput = new PostgresOutput(name, version, analysisOption.getDirectoryPath(), analysisOption.getPsqlServerName(), analysisOption.getPsqlPortNumber(), analysisOption.getPsqlDatabaseName(), analysisOption.getPsqlUser(), analysisOption.getPsqlPassword());
	}

	private void addBuiltinDepdendencies(Module module, AnalysisOptions analysisOption, List<Module> modules) {
		for (Module lbModule : modules) {
			if (lbModule.getLibraryType() == LibraryType.BUILT_IN) {
				String[] path = lbModule.getCanonicalPath().split("/");
				module.addDependency(FileUtil.getElementsOf(path, path.length - 1, path.length - 1).replace(".js", ""), lbModule, null);
			}
		}
	}

	private void checkForBeingLibrary(Module module, AnalysisOptions analysisOption) {
		try {
			if (analysisOption.getLibraries() != null && analysisOption.getLibraries().size() > 0)
				for (String library : analysisOption.getLibraries())
					if (new File(module.getSourceFile().getOriginalPath()).getCanonicalPath().contains(new File(library).getCanonicalPath())) {
						module.setAsLibrary(LibraryType.EXTERNAL_LIBRARY);
						return;
					}

			// for libraries with path such as Node's global modules
			// if (analysisOption.getLibrariesWithPath() != null &&
			// analysisOption.getLibrariesWithPath().size() > 0)
			// for (String library : analysisOption.getLibrariesWithPath())
			// if (module.getSourceFile().getOriginalPath().contains(library)) {
			// module.setAsLibrary(LibraryType.EXTERNAL_LIBRARY);
			// return;
			//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void markBuiltinLibraries(Module module, AnalysisOptions analysisOption) {
		if (analysisOption.getBuiltInLibraries() != null && analysisOption.getBuiltInLibraries().size() > 0)
			for (String library : analysisOption.getBuiltInLibraries())
				try {
					if (new File(module.getSourceFile().getOriginalPath()).getCanonicalPath().contains(new File(library).getCanonicalPath())) {
						module.setAsLibrary(LibraryType.BUILT_IN);
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	private boolean containsError(SourceFile sourceFile, Result result) {
		for (JSError error : result.errors) {
			if (error.sourceName.equals(sourceFile.getOriginalPath()))
				return true;
		}
		return false;
	}

	public ImmutableList<SourceFile> getInputs() {
		return this.inputs;
	}

	public void setInputs(ImmutableList<SourceFile> inputs) {
		this.inputs = inputs;
	}

}
