package ca.concordia.javascript.analysis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.decomposition.ClassDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.decomposition.Statement;
import ca.concordia.javascript.analysis.module.PackageExporter;
import ca.concordia.javascript.analysis.module.PackageImporter;
import ca.concordia.javascript.analysis.module.PackageSystem;
import ca.concordia.javascript.analysis.module.closurelibrary.ClosureLibraryExportHelper;
import ca.concordia.javascript.analysis.module.closurelibrary.ClosureLibraryImportHelper;
import ca.concordia.javascript.analysis.module.commonjs.CommonJSExportHelper;
import ca.concordia.javascript.analysis.module.commonjs.CommonJSRequireHelper;
import ca.concordia.javascript.language.PredefinedClasses;
import ca.concordia.javascript.language.PredefinedFunctions;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());

	public static void processFunctionDeclarationsToFindClasses(Module module) {
		Program program = module.getProgram();
		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (objectCreation.getOperandOfNewName() == null || objectCreation.isFunctionObject())
				continue;

			if (!findFunctionDeclaration(objectCreation, module)) {
				findPredefinedClasses(program, objectCreation, module);
			}
		}

		// Class inference
		ClassInferenceEngine.run(module);

	}

	public static void processModules(Module module, List<Module> modules, PackageSystem packageSystem) {
		PackageImporter packageImporter = null;
		PackageExporter packageExporter = null;
		switch (packageSystem) {
		case CommonJS:
			packageImporter = new CommonJSRequireHelper(module, modules);
			packageExporter = new CommonJSExportHelper(module, modules);
			break;
		case ClosureLibrary:
			packageImporter = new ClosureLibraryImportHelper(module, modules);
			packageExporter = new ClosureLibraryExportHelper(module, modules);
			break;
		default:
			break;
		}

		Program program = module.getProgram();
		for (SourceElement element : program.getSourceElements()) {
			if (element instanceof Statement) {
				Statement statement = (Statement) element;
				packageImporter.extract(statement.getStatement());
				packageExporter.extract(statement.getStatement());
			}
		}
	}

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
				for (Map.Entry<String, Module> dependency : module.getDependencies().entries()) {
					if (dependency.getKey().equals(functionInvocation.getIdentifier().asCompositeIdentifier().getMostLeftPart().toString())) {
						for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
							if (functionDeclaration.getName().contains(functionInvocation.getAliasedIdentifier().asCompositeIdentifier().getRightPart().toString()))
								//if (functionDeclaration.getName().toLowerCase().contains(functionInvocation.getAliasedIdentifier().toString().toLowerCase()))
								functionInvocation.setFunctionDeclaration(functionDeclaration, dependency.getValue());
						}
					}
				}
		}
	}

	private static boolean nodeSpecificFunction(FunctionInvocation functionInvocation, Multimap<String, Module> map) {
		for (Map.Entry<String, Module> dependency : map.entries()) {
			if (dependency.getKey().equals("module"))
				if (inspectFunctions(functionInvocation, dependency.getValue()))
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
		for (Entry<String, Module> dependency : module.getDependencies().entries()) {
			if (objectCreation.getIdentifier() instanceof CompositeIdentifier && objectCreation.getIdentifier().asCompositeIdentifier().getMostLeftPart().equals(dependency.getKey()) || objectCreation.getIdentifier().toString().equals(dependency.getKey()))
				for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
					AbstractIdentifier objectCreationIdentifier = null;
					if (objectCreation.getIdentifier() instanceof CompositeIdentifier)
						objectCreationIdentifier = objectCreation.getIdentifier().asCompositeIdentifier().getRightPart();
					else
						objectCreationIdentifier = objectCreation.getIdentifier();
					findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreationIdentifier, functionDeclaration, dependency.getValue());
					if (findMatch)
						return true;
				}
		}
		return findMatch;
	}

	private static boolean matchFunctionDeclarationAndObjectCreation(ObjectCreation objectCreation, AbstractIdentifier aliasedObjectCreation, FunctionDeclaration functionDeclaration, Module module) {
		String functionQualifiedName = functionDeclaration.getQualifiedName();

		//		if (functionDeclaration instanceof FunctionDeclarationExpression) {
		//			FunctionDeclarationExpression functionDeclarationExpression = (FunctionDeclarationExpression) functionDeclaration;
		//			if (functionDeclarationExpression.getFunctionDeclarationExpressionNature() == FunctionDeclarationExpressionNature.IIFE)
		//				return false;
		//		}
		if (functionQualifiedName.equals(aliasedObjectCreation.toString())) {
			functionDeclaration.setClassDeclaration(true);

			boolean hasNamespace = false;
			if (functionDeclaration instanceof FunctionDeclarationExpression)
				hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();
			ClassDeclaration classDeclaration = new ClassDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, false, hasNamespace, module.getLibraryType(), !objectCreation.getAliasedIdentifier().equals(objectCreation.getIdentifier()));
			objectCreation.setClassDeclaration(classDeclaration, module);
			module.addClass(classDeclaration);
			return true;
		}
		if (functionQualifiedName.equals(objectCreation.getIdentifier().toString())) {
			functionDeclaration.setClassDeclaration(true);

			boolean hasNamespace = false;
			if (functionDeclaration instanceof FunctionDeclarationExpression)
				hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();
			ClassDeclaration classDeclaration = new ClassDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, false, hasNamespace, module.getLibraryType(), !objectCreation.getAliasedIdentifier().equals(objectCreation.getIdentifier()));
			objectCreation.setClassDeclaration(classDeclaration, module);
			module.addClass(classDeclaration);
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
