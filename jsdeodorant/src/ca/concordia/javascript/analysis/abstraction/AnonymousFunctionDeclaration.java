package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class AnonymousFunctionDeclaration extends Function {
	private AbstractExpression leftOperand;

	public AnonymousFunctionDeclaration(AbstractExpression leftOperand,
			FunctionDeclaration functionDeclaration) {
		this.leftOperand = leftOperand;
		this.setKind(functionDeclaration.getKind());
		this.setParameters(functionDeclaration.getParameters());
		this.setBody(functionDeclaration.getBody());
	}

	public AbstractExpression getLeftOperand() {
		return leftOperand;
	}
}
