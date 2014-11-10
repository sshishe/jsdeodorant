package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class FunctionInvocation {
	private String memberName;
	private AbstractExpression operand;
	private List<AbstractExpression> arguments;

	public FunctionInvocation(String memberName, AbstractExpression operand,
			List<AbstractExpression> arguments) {

	}
}
