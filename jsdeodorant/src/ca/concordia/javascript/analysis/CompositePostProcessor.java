package ca.concordia.javascript.analysis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.Statement;
import ca.concordia.javascript.analysis.module.ExportHelper;
import ca.concordia.javascript.analysis.module.RequireHelper;
import ca.concordia.javascript.language.PredefinedClasses;
import ca.concordia.javascript.language.PredefinedFunctions;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());

	public static void processFunctionDeclarationsToFindClasses(Module module) {
		Program program = module.getProgram();
		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (objectCreation.getOperandOfNewName() == null || objectCreation.isFunctionObject())
				continue;
			if (!findPredefinedClasses(program, objectCreation)) {
				findFunctionDeclaration(objectCreation, module);
			}
		}
	}

	public static void processModules(Module module, List<Module> modules) {
		RequireHelper requireHelper = new RequireHelper(module, modules);
		ExportHelper exportHelper = new ExportHelper(module, modules);
		Program program = module.getProgram();
		for (SourceElement element : program.getSourceElements()) {
			if (element instanceof Statement) {
				Statement statement = (Statement) element;
				requireHelper.extract(statement.getStatement());
				exportHelper.extract(statement.getStatement());
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

			for (Map.Entry<String, Module> dependency : module.getDependencies().entrySet()) {
				// If the invocation is not Composite, it may be not part of the exports or module.exports
				if (!(functionInvocation.getIdentifier() instanceof CompositeIdentifier))
					continue;

				if (dependency.getKey().equals(functionInvocation.getIdentifier().asCompositeIdentifier().getMostLeftPart().toString())) {
					for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
						if (functionDeclaration.getName().contains(functionInvocation.getAliasedIdentifier().asCompositeIdentifier().getRightPart().toString()))
							functionInvocation.setFunctionDeclaration(functionDeclaration, dependency.getValue());
					}
				}
			}
		}
	}

	private static boolean findPredefinedClasses(Program program, ObjectCreation objectCreation) {
		if (PredefinedClasses.contains(objectCreation.getOperandOfNewName())) {
			objectCreation.setClassDeclarationPredefined(true);
			return true;
		}
		return false;
	}

	private static boolean findFunctionDeclaration(ObjectCreation objectCreation, Module module) {
		boolean findMatch = false;
		for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
			findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreation.getAliasedIdentifier(), functionDeclaration, module);
		}
		for (Entry<String, Module> dependency : module.getDependencies().entrySet()) {
			if (objectCreation.getIdentifier() instanceof CompositeIdentifier && objectCreation.getIdentifier().asCompositeIdentifier().getMostLeftPart().equals(dependency.getKey()))
				for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
					findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreation.getIdentifier().asCompositeIdentifier().getRightPart(), functionDeclaration, module);
				}
		}
		return findMatch;
	}

	private static boolean matchFunctionDeclarationAndObjectCreation(ObjectCreation objectCreation, AbstractIdentifier aliasedObjectCreation, FunctionDeclaration functionDeclaration, Module module) {
		String functionQualifiedName = functionDeclaration.getQualifiedName();
		if (functionQualifiedName.equals(aliasedObjectCreation.toString())) {
			functionDeclaration.setClassDeclaration(true);
			objectCreation.setClassDeclaration(functionDeclaration, module);
			return true;
		}
		return false;
	}
}
