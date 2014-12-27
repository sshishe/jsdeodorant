package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class ObjectCreation extends Creation {
	private NewExpressionTree newExpressionTree;
	private Function functionDeclaration;
	private String className;
	private List<AbstractExpression> arguments;

	public ObjectCreation(NewExpressionTree newExpressionTree,
			String className, List<AbstractExpression> arguments) {
		this.newExpressionTree = newExpressionTree;
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

	public NewExpressionTree getNewExpressionTree() {
		return newExpressionTree;
	}

	public void setNewExpressionTree(NewExpressionTree newExpressionTree) {
		this.newExpressionTree = newExpressionTree;
	}
}
