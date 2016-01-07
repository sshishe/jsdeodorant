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
import ca.concordia.javascript.analysis.util.CSVFileWriter;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.PredefinedClasses;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());
	private static CSVFileWriter csvWriter;

	public static void processFunctionDeclarationsToFindClasses(Module module) {
		Program program = module.getProgram();
		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (objectCreation.getClassName() == null || objectCreation.isFunctionObject())
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
			AbstractIdentifier functionInvocationIdentifier = IdentifierHelper.getIdentifier(functionInvocation.getCallExpressionTree());

			// First find declaration within the current module
			for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
				if (functionDeclaration.getName().contains(functionInvocationIdentifier.toString()))
					functionInvocation.setFunctionDeclaration(functionDeclaration);
			}

			// If the function declaration is already found, skip the rest for cross-module detection
			if (functionInvocation.getFunctionDeclaration() != null)
				continue;

			for (Map.Entry<String, Module> dependency : module.getDependencies().entrySet()) {
				// If the invocation is not Composite, it may be not part of the exports or module.exports
				if (!(functionInvocationIdentifier instanceof CompositeIdentifier))
					continue;

				if (dependency.getKey().equals(functionInvocationIdentifier.asCompositeIdentifier().getMostLeftPart().toString())) {
					for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
						if (functionDeclaration.getName().contains(functionInvocationIdentifier.asCompositeIdentifier().getRightPart().toString()))
							functionInvocation.setFunctionDeclaration(functionDeclaration);
					}
				}
			}
		}
	}

	private static boolean findPredefinedClasses(Program program, ObjectCreation objectCreation) {
		if (PredefinedClasses.contains(objectCreation.getClassName())) {
			objectCreation.setClassDeclarationPredefined(true);
			return true;
		}
		return false;
	}

	private static boolean findFunctionDeclaration(ObjectCreation objectCreation, Module module) {
		boolean findMatch = false;
		for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
			findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreation.getAliasedIdentifier(), functionDeclaration);
		}
		for (Entry<String, Module> dependency : module.getDependencies().entrySet()) {
			if (objectCreation.getIdentifier() instanceof CompositeIdentifier && objectCreation.getIdentifier().asCompositeIdentifier().getMostLeftPart().equals(dependency.getKey()))
				for (FunctionDeclaration functionDeclaration : dependency.getValue().getProgram().getFunctionDeclarationList()) {
					findMatch = matchFunctionDeclarationAndObjectCreation(objectCreation, objectCreation.getIdentifier().asCompositeIdentifier().getRightPart(), functionDeclaration);
				}
		}
		return findMatch;
	}

	private static boolean matchFunctionDeclarationAndObjectCreation(ObjectCreation objectCreation, AbstractIdentifier aliasedObjectCreation, FunctionDeclaration functionDeclaration) {
		String functionQualifiedName = functionDeclaration.getQualifiedName();
		if (functionQualifiedName.equals(aliasedObjectCreation.toString())) {
			functionDeclaration.setClassDeclaration(true);
			objectCreation.setClassDeclaration(functionDeclaration);
			return true;
		}
		return false;
	}
}
