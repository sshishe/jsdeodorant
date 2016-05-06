package ca.concordia.jsdeodorant.analysis.module.commonjs;

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

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.module.PackageImporter;
import ca.concordia.jsdeodorant.analysis.util.FileUtil;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;
import ca.concordia.jsdeodorant.analysis.util.JSONReader;
import ca.concordia.jsdeodorant.analysis.util.StringUtil;

public class CommonJSRequireHelper implements PackageImporter {
	static Logger log = Logger.getLogger(CommonJSRequireHelper.class.getName());
	private AbstractIdentifier requireIdentifier;
	private File moduleFile;
	private Module currentModule;
	private List<Module> modules;

	public CommonJSRequireHelper(Module module, List<Module> modules) {
		this.currentModule = module;
		this.modules = modules;
	}

	public void extract(ParseTree expression) {
		if (expression instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = expression.asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatement.declarations.declarations) {
				if (checkIfInitializerIsRequireStatement(variableDeclaration)) {
					matchModules(new AbstractExpression(expression));
				}
			}
		} else if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof BinaryOperatorTree)
				if (checkIfRValueIsRequireStatement(expressionStatement.expression.asBinaryOperator())) {
					matchModules(new AbstractExpression(expression));
					return;
				}
		}
	}

	private void matchModules(AbstractExpression abstractExpression) {
		try {
			List<String> files = null;
			if (moduleFile.isDirectory()) {
				files = FileUtil.getFilesInDirectory(moduleFile.getPath(), "js");
				for (String file : files) {
					matchWithCanonicalPath(new File(file).getCanonicalPath(), abstractExpression);
				}
			} else
				matchWithCanonicalPath(moduleFile.getCanonicalPath(), abstractExpression);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void matchWithCanonicalPath(String moduleCanonicalPath, AbstractExpression abstractExpression) throws IOException {
		for (Module module : modules) {
			if (new File(module.getSourceFile().getOriginalPath()).getCanonicalPath().equals(moduleCanonicalPath)) {
				currentModule.addDependency(requireIdentifier.toString(), module, abstractExpression);
				return;
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
			if (IdentifierHelper.getIdentifier(callExpression.operand).toString().equals("require")) {
				if (callExpression.arguments.arguments.size() > 0)
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
