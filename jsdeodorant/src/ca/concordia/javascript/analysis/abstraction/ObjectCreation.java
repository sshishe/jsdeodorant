package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class ObjectCreation extends Creation {
	private Function functionDeclaration;
	private String className;
	private List<AbstractExpression> arguments;

	public ObjectCreation(String className, List<AbstractExpression> arguments) {
		this.className = className;
		this.arguments = arguments;
	}

	public Function getFunctionDeclaration() {
		return functionDeclaration;
	}

	public void setFunctionDeclaration(Function functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

	public String getClassName() {
		return className;
	}

	public List<AbstractExpression> getArguments() {
		return arguments;
	}
}
