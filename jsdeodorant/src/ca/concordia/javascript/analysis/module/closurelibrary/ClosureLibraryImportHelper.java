package ca.concordia.javascript.analysis.module.closurelibrary;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.Export;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.module.PackageImporter;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class ClosureLibraryImportHelper implements PackageImporter {
	private Module currentModule;
	private List<Module> modules;

	public ClosureLibraryImportHelper(Module module, List<Module> modules) {
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
				if (operandIdentifier.toString().equals("goog.require")) {
					AbstractIdentifier importName = IdentifierHelper.getIdentifier(callExpression.arguments.arguments.get(0));
					for (Module module : modules) {
						if (module.equals(currentModule))
							continue;
						for (Export export : module.getExports()) {
							if (importName.toString().replace("'", "").equals(export.getName()))
								this.currentModule.addDependency(importName.toString().replace("'", ""), module);
						}
					}
				}
			}
		}

	}
}
