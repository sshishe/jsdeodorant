package ca.concordia.javascript.analysis.module;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.util.FileUtil;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.JSONReader;
import ca.concordia.javascript.analysis.util.StringUtil;

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
				if (new File(module.getSourceFile().getOriginalPath()).getCanonicalPath().equals(moduleFile.getCanonicalPath())) {
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
				if (moduleFile == null)
					return false;
				return true;
			}
		}
		return false;
	}

	private File normalizeModuleName(String moduleName) {
		String moduleNameWithExtension = moduleName.replace("\'", "");
		moduleName = moduleName.replace("\'", "");

		int i = moduleNameWithExtension.lastIndexOf('.');
		if (i <= 0)
			moduleNameWithExtension = moduleNameWithExtension + ".js";

		String[] currentModulePath = currentModule.getSourceFile().getOriginalPath().split("/");
		String path = FileUtil.getElementsOf(currentModulePath, 0, currentModulePath.length - 2);
		File file = new File(path + "/" + moduleNameWithExtension);

		if (file.exists())
			return file;
		else {
			file = locateTheFileInPredefinedFolders(path, moduleName);
		}

		return file;
	}

	/**
	 * For a module with name utils the following path would be searched: 1)
	 * ./node_modules/util.js 2) ./node_modules/utils/index.js 3)
	 * ./node_modules/utils/package.json
	 * 
	 * @param path
	 * @param moduleName
	 */
	private File locateTheFileInPredefinedFolders(String path, String moduleName) {
		File moduleFile = new File(path + "/node_modules/" + moduleName + ".js");
		if (moduleFile.exists())
			return moduleFile;
		moduleFile = new File(path + "/node_modules/" + moduleName + "/" + moduleName + ".js");
		if (moduleFile.exists())
			return moduleFile;
		moduleFile = new File(path + "/node_modules/" + moduleName + "/index.js");
		if (moduleFile.exists())
			return moduleFile;
		File packageConfigFile = new File(path + "/node_modules/" + moduleName + "/package.json");
		if (packageConfigFile.exists()) {
			JSONReader reader = new JSONReader();
			try {
				String mainFilePath = reader.getElementFromObject(packageConfigFile.getCanonicalPath(), "main");
				if (!StringUtil.isNullOrEmpty(mainFilePath))
					moduleFile = new File(path + "/node_modules/" + moduleName + "/" + mainFilePath);
				if (moduleFile.exists())
					return moduleFile;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
}