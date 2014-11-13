package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import ca.concordia.javascript.analysis.util.ExpressionExtractor;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class AbstractExpression extends AbstractFunctionFragment {

	private ParseTree expression;

	public AbstractExpression(ParseTree expression) {
		super(null);
		this.expression = expression;
	}

	public AbstractExpression(ParseTree expression, CompositeStatement parent) {
		super(parent);
		this.expression = expression;
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();

		processFunctionInvocations(expressionExtractor
				.getCallExpressions(expression));

		processFunctionDeclarations(expressionExtractor
				.getFunctionDeclarations(expression));

		// used by arrayCreations and objectCreations
		List<ParseTree> newExpressions = expressionExtractor
				.getNewExpressions(expression);

		List<ParseTree> objectCreations = expressionExtractor
				.getObjectLiteralExpressions(expression);
		objectCreations.addAll(newExpressions);
		processObjectCreations(objectCreations);

		List<ParseTree> arrayCreations = expressionExtractor
				.getArrayLiteralExpressions(expression);
		objectCreations.addAll(newExpressions);
		processArrayCreations(arrayCreations);
	}

	public String toString() {
		return expression.toString();
	}
}
