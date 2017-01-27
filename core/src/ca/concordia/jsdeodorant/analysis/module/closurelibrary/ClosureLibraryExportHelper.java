package ca.concordia.jsdeodorant.analysis.module.closurelibrary;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Export;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.module.PackageExporter;
import ca.concordia.jsdeodorant.analysis.module.commonjs.CommonJSExportHelper;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class ClosureLibraryExportHelper implements PackageExporter {
	static Logger log = Logger.getLogger(CommonJSExportHelper.class.getName());
	private Module currentModule;
	private Set<Module> modules;

	public ClosureLibraryExportHelper(Module module, Set<Module> modules) {
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
