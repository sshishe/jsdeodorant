package ca.concordia.javascript.analysis.module;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class RequireHelper {
	static Logger log = Logger.getLogger(RequireHelper.class.getName());
	private AbstractIdentifier requireIdentifier;
	private File moduleFile;
	private Module currentModule;
	private List<Module> modules;

	public RequireHelper(Module module, List<Module> modules) {
		this.currentModule = module;
		this.modules = modules;
	}

	public void extract(ParseTree expression) {
		if (expression instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = expression.asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatement.declarations.declarations) {
				if (checkIfInitializerIsRequireStatement(variableDeclaration)) {
					matchModules();
					return;
				}
			}
		}
		if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof BinaryOperatorTree)
				if (checkIfRValueIsRequireStatement(expressionStatement.expression.asBinaryOperator())) {
					matchModules();
					return;
				}
		}
	}

	private void matchModules() {
		for (Module module : modules) {
			try {
				if (module.getSourceFile().getOriginalPath().equals(moduleFile.getCanonicalFile().getPath())) {
					currentModule.addDependency(requireIdentifier.toString(), module);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkIfRValueIsRequireStatement(BinaryOperatorTree binaryOperator) {
		if (!IdentifierHelper.getIdentifier(binaryOperator.operator).equals("="))
			return false;
		if (checkIfCallExpressionIsRequireStatement(binaryOperator.right)) {
			requireIdentifier = IdentifierHelper.getIdentifier(binaryOperator.left);
			return true;
		}
		return false;
	}

	private boolean checkIfInitializerIsRequireStatement(VariableDeclarationTree variableDeclaration) {
		if (variableDeclaration.initializer == null)
			return false;
		if (checkIfCallExpressionIsRequireStatement(variableDeclaration.initializer)) {
			requireIdentifier = IdentifierHelper.getIdentifier(variableDeclaration.lvalue);
			return true;
		}
		return false;
	}

	private boolean checkIfCallExpressionIsRequireStatement(ParseTree node) {
		if (node instanceof CallExpressionTree) {
			CallExpressionTree callExpression = node.asCallExpression();
			if (IdentifierHelper.getIdentifier(callExpression.operand).equals("require")) {
				moduleFile = normalizeModuleName(IdentifierHelper.getIdentifier(callExpression.arguments.arguments.get(0)).getIdentifierName());
				return true;
			}
		}
		return false;
	}

	private File normalizeModuleName(String moduleName) {
		moduleName = moduleName.replace("\'", "");
		String[] currentModulePath = currentModule.getSourceFile().getOriginalPath().split("/");
		String p = getElementsOf(currentModulePath, 0, currentModulePath.length - 2);
		return new File(p + "/" + moduleName);
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
}
