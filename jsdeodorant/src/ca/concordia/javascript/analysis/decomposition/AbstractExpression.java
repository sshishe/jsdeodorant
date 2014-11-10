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
		List<ParseTree> functionInvocations = expressionExtractor
				.getCallExpressions(expression);

		processFunctionInvocations(functionInvocations);
	}

	public String toString() {
		return expression.toString();
	}
}
