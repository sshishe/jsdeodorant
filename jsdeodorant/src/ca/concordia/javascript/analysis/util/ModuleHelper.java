package ca.concordia.javascript.analysis.util;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.Module;

public class ModuleHelper {
	private String moduleNameExtracted;
	private Module module;
	private List<Module> modules;

	public boolean hasRequireStatement(Module module, List<Module> modules, ParseTree expression) {
		this.module = module;
		this.modules = modules;
		if (expression instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = expression.asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatement.declarations.declarations) {
				if (checkIfInitializerIsRequireStatement(variableDeclaration)) {
					// put the logic to locate modules here
					return true;
				}
			}
		}
		if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof BinaryOperatorTree)
				if (checkIfRValueIsRequireStatement(expressionStatement.expression.asBinaryOperator())) {
					// put the logic to locate modules here
					return true;
				}
		}
		return false;
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
				moduleNameExtracted = normalizeModuleName(IdentifierHelper
						.getIdentifier(callExpression.arguments.arguments.get(0)).getIdentifierName());
				return true;
			}

		}
		return false;
	}

	private String normalizeModuleName(String moduleName) {
		String[] splittedModuleName = moduleName.split("/");
		String rawModuleName;
		if (splittedModuleName.length > 1)
			rawModuleName = splittedModuleName[splittedModuleName.length - 1];
		else
			rawModuleName = splittedModuleName[0];
		return rawModuleName.replace("\'", "");
	}
}
