package ca.concordia.jsdeodorant.analysis.module.helma;

import java.util.List;
import java.util.Set;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.module.PackageImporter;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class HelmaRequireHelper implements PackageImporter {
	private Module currentModule;
	private Set<Module> modules;

	public HelmaRequireHelper(Module module, Set<Module> modules) {
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
				if (operandIdentifier.toString().equals("app.addRepository")) {
					String importName = (IdentifierHelper.getIdentifier(callExpression.arguments.arguments.get(0))).toString().replace("'", "").replace("\"", "");
					importName=importName.replace("./", "");
					int matchedModules=0;
					for (Module module : modules) {
						if (!module.equals(currentModule)){
							if (module.getSourceFile().toString().endsWith(importName)){
								matchedModules++;
							}
						}
						if (matchedModules==1){
							this.currentModule.addDependency(importName, module, new AbstractExpression(expressionStatement.expression));
						}
					}
				}
			}
		}

	}
}

