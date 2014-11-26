package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class AnonymousFunctionDeclaration implements SourceElement {
	private AbstractExpression leftOperand;
	private FunctionDeclaration functionDeclaration;

	public AnonymousFunctionDeclaration(AbstractExpression leftOperand,
			FunctionDeclaration functionDeclaration) {
		this.leftOperand = leftOperand;
		this.functionDeclaration = functionDeclaration;
	}

	public AbstractExpression getLeftOperand() {
		return leftOperand;
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}
}
