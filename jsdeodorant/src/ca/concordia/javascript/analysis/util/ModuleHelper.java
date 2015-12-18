package ca.concordia.javascript.analysis.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.Module;

public class ModuleHelper {
	static Logger log = Logger.getLogger(ModuleHelper.class.getName());
	private String moduleNameExtracted;
	private Module currentModule;
	private List<Module> modules;

	public boolean hasRequireStatement(Module module, List<Module> modules, ParseTree expression) {
		this.currentModule = module;
		this.modules = modules;
		if (expression instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = expression.asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatement.declarations.declarations) {
				if (checkIfInitializerIsRequireStatement(variableDeclaration)) {
					findModule(moduleNameExtracted);
					return true;
				}
			}
		}
		if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof BinaryOperatorTree)
				if (checkIfRValueIsRequireStatement(expressionStatement.expression.asBinaryOperator())) {
					findModule(moduleNameExtracted);
					return true;
				}
		}
		return false;
	}

	private void findModule(String moduleName) {
		log.warn("Module name is:" + moduleName);
		for (Module module : modules) {
			if (matchParts(module.getSourceFile().getOriginalPath().split("/"), moduleName.split("/"))) {
				currentModule.addDependency(module);
			}
		}
	}

	private boolean checkIfRValueIsRequireStatement(BinaryOperatorTree binaryOperator) {
		if (!IdentifierHelper.getIdentifier(binaryOperator.operator).equals("="))
			return false;
		return checkIfCallExpressionIsRequireStatement(binaryOperator.right);
	}

	private boolean checkIfInitializerIsRequireStatement(VariableDeclarationTree variableDeclaration) {
		if (variableDeclaration.initializer == null)
			return false;
		return checkIfCallExpressionIsRequireStatement(variableDeclaration.initializer);
	}

	private boolean checkIfCallExpressionIsRequireStatement(ParseTree node) {
		if (node instanceof CallExpressionTree) {
			CallExpressionTree callExpression = node.asCallExpression();
			if (IdentifierHelper.getIdentifier(callExpression.operand).equals("require")) {
				moduleNameExtracted = normalizeModuleName(IdentifierHelper.getIdentifier(callExpression.arguments.arguments.get(0)).getIdentifierName());
				return true;
			}
		}
		return false;
	}

	private String normalizeModuleName(String moduleName) {
		moduleName = moduleName.replace("\'", "");
		StringBuilder normalizedPath = new StringBuilder();
		String[] splittedModuleName = moduleName.split("/");
		for (int index = 0; index < splittedModuleName.length - 1; index++) {
			String part = splittedModuleName[index];
			if (part.equals(".")) {
				String[] splittedOriginalPath = currentModule.getSourceFile().getOriginalPath().split("/");
				String path = getElementsOf(splittedOriginalPath, 0, splittedOriginalPath.length - 2);
				normalizedPath.append(path);
				normalizedPath.append(getElementsOf(splittedModuleName, index + 1, splittedModuleName.length - 1));
			}
		}
		return normalizedPath.toString();

	}

	private String getElementsOf(String[] source, int from, int to) {
		StringBuilder path = new StringBuilder();
		for (int index = from; index <= to; index++) {
			path.append(source[index]);
			if (from != to)
				path.append("/");
		}
		return path.toString();
	}

	private boolean matchParts(String[] existingModuleParts, String[] nameParts) {
		for (int pathLevel = nameParts.length - 1; pathLevel > 0; pathLevel--) {
			for (int pathLevelForExistingModule = existingModuleParts.length - 1; pathLevelForExistingModule > nameParts.length; pathLevelForExistingModule--) {
				if (!nameParts[pathLevel].equals(existingModuleParts[pathLevelForExistingModule]))
					return false;
			}
		}
		return true;
	}
}
