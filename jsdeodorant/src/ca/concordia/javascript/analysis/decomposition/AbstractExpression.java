package ca.concordia.javascript.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class AbstractExpression extends AbstractFunctionFragment {

	private ParseTree expression;

	protected AbstractExpression(ParseTree expression) {
		this.expression = expression;
		processExpression(expression);
	}

	protected AbstractExpression(ParseTree expression, CompositeStatement parent) {
		this.expression = expression;
		processExpression(expression);
	}

	private void processExpression(ParseTree expression) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return expression.toString();
	}
}
