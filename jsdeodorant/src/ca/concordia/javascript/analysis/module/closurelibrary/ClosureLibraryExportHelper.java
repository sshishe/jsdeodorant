package ca.concordia.javascript.analysis.module.closurelibrary;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.Export;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.module.PackageExporter;
import ca.concordia.javascript.analysis.module.commonjs.CommonJSExportHelper;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class ClosureLibraryExportHelper implements PackageExporter {
	static Logger log = Logger.getLogger(CommonJSExportHelper.class.getName());
	private Module currentModule;
	private List<Module> modules;

	public ClosureLibraryExportHelper(Module module, List<Module> modules) {
		this.currentModule = module;
		this.modules = modules;
	}

	@Override
	public void extract(ParseTree expression) {
		if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof CallExpressionTree) {
				CallExpressionTree callExpression = expressionStatement.expression.asCallExpression();
				AbstractIdentifier operandIdentifier = IdentifierHelper.getIdentifier(callExpression.operand);
				if (operandIdentifier.toString().equals("goog.provide")) {
					AbstractIdentifier exportName = IdentifierHelper.getIdentifier(callExpression.arguments.arguments.get(0));
					Export export = new Export(exportName.toString().replace("'", ""), new AbstractExpression(callExpression));
					currentModule.addExport(export);
				}
			}
		}
	}

}
