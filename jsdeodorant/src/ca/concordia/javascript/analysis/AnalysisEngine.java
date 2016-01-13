package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.module.LibraryType;
import ca.concordia.javascript.experiment.CSVOutput;
import ca.concordia.javascript.metrics.CyclomaticComplexity;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;

public class AnalysisEngine {
	static Logger log = Logger.getLogger(AnalysisEngine.class.getName());
	private final ExtendedCompiler compiler;
	private final CompilerOptions compilerOptions;
	private ImmutableList<SourceFile> inputs;
	private ImmutableList<SourceFile> externs;

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions) {
		this(compiler, compilerOptions, null, null);
	}

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions, ImmutableList<SourceFile> inputs, ImmutableList<SourceFile> externs) {
		this.compiler = compiler;
		this.compilerOptions = compilerOptions;
		this.compilerOptions.setIdeMode(true);
		this.compilerOptions.skipAllCompilerPasses();
		CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(this.compilerOptions);
		WarningLevel warningLevel = WarningLevel.QUIET;
		warningLevel.setOptionsForWarningLevel(this.compilerOptions);
		this.inputs = inputs;
		this.externs = externs;
	}

	public List<Module> run(AnalysisOptions analysisOption) {
		Result result = compiler.compile(externs, inputs, compilerOptions);
		ScriptParser scriptAnalyzer = new ScriptParser(compiler);
		List<Module> modules = new ArrayList<>();
		if (analysisOption.isOutputToCSV()) {
			CSVOutput.createAndClearFolder("log/functions");
			CSVOutput.createAndClearFolder("log/classes");
		}

		for (SourceFile sourceFile : inputs) {
			if (containsError(sourceFile, result))
				continue;
			Program program = new Program();
			ProgramTree programTree = scriptAnalyzer.parse(sourceFile);
			for (ParseTree sourceElement : programTree.sourceElements) {
				StatementProcessor.processStatement(sourceElement, program);
			}
			modules.add(new Module(program, sourceFile, scriptAnalyzer.getMessages()));
		}

		for (Module module : modules) {
			checkForBeingLibrary(module, analysisOption);

			if (analysisOption.hasModuleAnalysis())
				CompositePostProcessor.processModules(module, modules);

			if (analysisOption.hasClassAnlysis())
				if (analysisOption.analyzeLibrariesForClasses() && module.getLibraryType() != LibraryType.BUILT_IN)
					CompositePostProcessor.processFunctionDeclarationsToFindClasses(module);

			CompositePostProcessor.processFunctionInvocations(module);

			if (analysisOption.isCalculateCyclomatic()) {
				CyclomaticComplexity cyclomaticComplexity = new CyclomaticComplexity(module.getProgram());

				for (Map.Entry<String, Integer> entry : cyclomaticComplexity.calculate().entrySet()) {
					log.warn("Cyclomatic Complexity of " + entry.getKey() + " is: " + entry.getValue());
				}
			}

			if (analysisOption.isOutputToCSV()) {
				CSVOutput experimentOutput = new CSVOutput(module);
				experimentOutput.functionSignatures();
				experimentOutput.functionInvocations();
				experimentOutput.uniqueClassDeclaration();
			}
			AnalysisResult.addPackageInstance(module);
		}
		CSVOutput experimentOutput = new CSVOutput();
		experimentOutput.aggregateReportForModule(modules);
		log.info("Total number of classes: " + AnalysisResult.getTotalNumberOfClasses());
		log.info("Total number of files: " + AnalysisResult.getTotalNumberOfFiles());
		return modules;
	}

	private void checkForBeingLibrary(Module module, AnalysisOptions analysisOption) {
		if (analysisOption.getLibraries() != null && analysisOption.getLibraries().size() > 0)
			for (String library : analysisOption.getLibraries())
				if (module.getSourceFile().getOriginalPath().contains(library)) {
					module.setAsLibrary(LibraryType.EXTERNAL_LIBRARY);
					return;
				}
		// for libraries with path such as Node's built-in modules
		for (String library : analysisOption.getLibrariesWithPath())
			if (module.getSourceFile().getOriginalPath().contains(library)) {
				module.setAsLibrary(LibraryType.BUILT_IN);
				return;
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
