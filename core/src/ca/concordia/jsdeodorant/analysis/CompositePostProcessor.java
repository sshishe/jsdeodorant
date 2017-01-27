package ca.concordia.jsdeodorant.analysis;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.FunctionInvocation;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclaration;
import ca.concordia.jsdeodorant.language.PredefinedClasses;
import ca.concordia.jsdeodorant.language.PredefinedFunctions;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());

	public static void processFunctionDeclarationsToFindClasses(Module module, ClassAnalysisMode classInferenceMode) {
		Program program = module.getProgram();
		log.debug("Analysing ObjectCreation in: " + module.getSourceFile().getName());
		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (objectCreation.getOperandOfNewName() == null || objectCreation.isFunctionObject())
				continue;

			if (!findFunctionDeclaration(objectCreation, module)) {
				findPredefinedClasses(program, objectCreation, module);
			}
		}
		log.debug("Performing  ClassInference analysis in: " + module.getSourceFile().getName());
		// Class inference
		if(classInferenceMode == ClassAnalysisMode.NON_STRICT)
			ClassInferenceEngine.run(module);
		else if(classInferenceMode == ClassAnalysisMode.STRICT)
			ClassInferenceEngineStricMode.run(module);
		else
			System.err.println("class Inference mode need to be set");
	}

	// MOVED TO JSPRoject
//	public static void processModules(Module module, List<Module> modules, PackageSystem packageSystem, boolean onlyExports) {
//		PackageImporter packageImporter = null;
//		PackageExporter packageExporter = null;
//		switch (packageSystem) {
//		case CommonJS:
//			if (!onlyExports)
//				packageImporter = new CommonJSRequireHelper(module, modules);
//			packageExporter = new CommonJSExportHelper(module, modules);
//			break;
//		case ClosureLibrary:
//			if (!onlyExports)
//				packageImporter = new ClosureLibraryImportHelper(module, modules);
//			packageExporter = new ClosureLibraryExportHelper(module, modules);
//			break;
//		default:
//			break;
//		}
//
//		Program program = module.getProgram();
//		for (SourceElement element : program.getSourceElements()) {
//			if (element instanceof Statement) {
//				Statement statement = (Statement) element;
//				if (!onlyExports)
//					packageImporter.extract(statement.getStatement());
//				packageExporter.extract(statement.getStatement());
//			}
//		}
//	}

	public static void processFunctionInvocations(Module module) {
		Program program = module.getProgram();
		for (FunctionInvocation functionInvocation : program.getFunctionInvocationList()) {

			// First find declaration within the current module
			for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
				if (functionDeclaration.getName().contains(functionInvocation.getAliasedIdentifier().toString()))
					functionInvocation.setFunctionDeclaration(functionDeclaration, module);
			}

			// If the function declaration is already found, skip the rest for cross-module detection
			if (functionInvocation.getFunctionDeclaration() != null)
				continue;

			// Check if the function is predefined JavaScript function
			functionInvocation.setPredefinedState(PredefinedFunctions.isItPredefined(functionInvocation.getPredefinedName()));

			if (functionInvocation.isPredefined())
				continue;

			// Node specific function which are not qualified names
			// If the invocation is not Composite, it may be not part of the exports or module.exports
			if (functionInvocation.getIdentifier() instanceof PlainIdentifier) {
				if (nodeSpecificFunction(functionInvocation, module.getDependencies()))
					continue;
			} else
				for (Dependency dependency : module.getDependencies()) {
					if (dependency.getName().equals(functionInvocation.getIdentifier().asCompositeIdentifier().getMostLeftPart().toString())) {
						for (FunctionDeclaration functionDeclaration : dependency.getDependency().getProgram().getFunctionDeclarationList()) {
							if (functionDeclaration.getName().contains(functionInvocation.getAliasedIdentifier().asCompositeIdentifier().getRightPart().toString()))
								//if (functionDeclaration.getName().toLowerCase().contains(functionInvocation.getAliasedIdentifier().toString().toLowerCase()))
								functionInvocation.setFunctionDeclaration(functionDeclaration, dependency.getDependency());
						}
					}
				}
		}
	}

	private static boolean nodeSpecificFunction(FunctionInvocation functionInvocation, Set<Dependency> dependencies) {
		for (Dependency dependency : dependencies) {
			if (dependency.getName().equals("module"))
				if (inspectFunctions(functionInvocation, dependency.getDependency()))
					return true;
		}
		return false;
	}

	private static boolean inspectFunctions(FunctionInvocation functionInvocation, Module dependency) {
		for (FunctionDeclaration functionDeclaration : dependency.getProgram().getFunctionDeclarationList()) {
			if (functionDeclaration.getName().toLowerCase().contains(functionInvocation.getAliasedIdentifier().toString().toLowerCase())) {
				functionInvocation.setFunctionDeclaration(functionDeclaration, dependency);
				if (functionInvocation.getFunctionDeclaration() != null)
					return true;
			}
		}
		return false;
	}

	private static boolean findPredefinedClasses(Program program, ObjectCreation objectCreation, Module module) {
		if (PredefinedClasses.contains(objectCreation.getOperandOfNewName())) {
			objectCreation.setClassDeclarationPredefined(true);
			//ClassAnalysisReport.addPredefinedClass(objectCreation, module);
			return true;
		}
		return false;
	}

	private static boolean findFunctionDeclaration(ObjectCreation objectCreation, Module module) {
		boolean findMatch = false;
		for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
			findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreation.getAliasedIdentifier(), functionDeclaration, module);
			if (findMatch)
				return true;
		}
		for (Dependency dependency : module.getDependencies()) {
			if (objectCreation.getIdentifier() instanceof CompositeIdentifier && objectCreation.getIdentifier().asCompositeIdentifier().getMostLeftPart().equals(dependency.getName()) || objectCreation.getIdentifier().toString().equals(dependency.getName()))
				for (FunctionDeclaration functionDeclaration : dependency.getDependency().getProgram().getFunctionDeclarationList()) {
					AbstractIdentifier objectCreationIdentifier = null;
					if (objectCreation.getIdentifier() instanceof CompositeIdentifier)
						objectCreationIdentifier = objectCreation.getIdentifier().asCompositeIdentifier().getRightPart();
					else
						objectCreationIdentifier = objectCreation.getIdentifier();
					findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreationIdentifier, functionDeclaration, dependency.getDependency());
					if (findMatch)
						return true;
				}
		}
		return findMatch;
	}

	private static boolean matchFunctionDeclarationAndObjectCreation(ObjectCreation objectCreation, AbstractIdentifier aliasedObjectCreation, FunctionDeclaration functionDeclaration, Module module) {
		String functionQualifiedName = functionDeclaration.getQualifiedName();

		if (functionQualifiedName.equals(aliasedObjectCreation.toString())) {
			TypeDeclaration classDeclaration=module.createTypeDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, false, !objectCreation.getAliasedIdentifier().equals(objectCreation.getIdentifier()));
			objectCreation.setClassDeclaration(classDeclaration, module);
			return true;
		}
		if (functionQualifiedName.equals(objectCreation.getIdentifier().toString()) || functionDeclaration.getRawIdentifier() != null && functionDeclaration.getRawIdentifier().toString().equals(objectCreation.getIdentifier().toString())) {
			TypeDeclaration classDeclaration=module.createTypeDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, false, !objectCreation.getAliasedIdentifier().equals(objectCreation.getIdentifier()));
			objectCreation.setClassDeclaration(classDeclaration, module);
			return true;
		}
		return false;
	}

	public static void addDepndenciesBlindly(Module module, List<Module> modules) {
		for (Module dependency : modules) {
			//module.addDependency(module.get, dependency);
		}

	}
}
