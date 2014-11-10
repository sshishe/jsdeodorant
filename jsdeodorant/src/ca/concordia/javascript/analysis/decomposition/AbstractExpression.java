package ca.concordia.javascript.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class AbstractExpression extends AbstractFunctionFragment {

	private ParseTree expression;

	public AbstractExpression(ParseTree expression) {
		super(null);
		this.expression = expression;
		processExpression(expression);
	}

	public AbstractExpression(ParseTree expression, CompositeStatement parent) {
		super(parent);
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
