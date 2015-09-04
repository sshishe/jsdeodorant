package ca.concordia.javascript.analysis.util;

import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;

public class ModelHelper {
	static Logger log = Logger.getLogger(ModelHelper.class.getName());

	public static AbstractStatement getStatementContainingFunctionDeclaration(FunctionDeclarationExpression functionDeclarationExpression) {
		SourceContainer container = functionDeclarationExpression.getParent();
		if (container instanceof Program) {
			Program program = (Program) container;
			List<SourceElement> elements = program.getSourceElements();
			for (SourceElement sourceElement : elements) {
				if (sourceElement instanceof AbstractStatement) {
					if (isContainFunctionDeclarationExpression((AbstractStatement) sourceElement, functionDeclarationExpression))
						return (AbstractStatement) sourceElement;
				}
			}
		} else if (container instanceof CompositeStatement) {
			CompositeStatement composite = (CompositeStatement) container;
			List<AbstractStatement> statements = composite.getStatements();
			for (AbstractStatement statement : statements) {
				if (isContainFunctionDeclarationExpression(statement, functionDeclarationExpression))
					return statement;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param statement
	 * @param functionDeclarationExpression
	 * @return
	 */
	private static boolean isContainFunctionDeclarationExpression(AbstractStatement statement, FunctionDeclarationExpression functionDeclarationExpression) {
		for (FunctionDeclarationExpression functionDeclaration : statement.getFunctionDeclarationExpressionList()) {
			if (functionDeclaration.equals(functionDeclarationExpression))
				return true;
		}
		return false;
	}
}
