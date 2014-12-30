package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.QualifiedNameExtractor;

public class ObjectCreation extends Creation {
	private NewExpressionTree newExpressionTree;
	private Function functionDeclaration;
	private AbstractExpression operandOfNew;
	private List<AbstractExpression> arguments;

	public ObjectCreation(){
		
	}
	
	/**
	 * 
	 * @param newExpressionTree
	 * @param operandOfNew
	 *            the name of class that would instantiate
	 * @param arguments
	 *            passed to constructor
	 */
	public ObjectCreation(NewExpressionTree newExpressionTree,
			AbstractExpression operandOfNew, List<AbstractExpression> arguments) {
		this.newExpressionTree = newExpressionTree;
		this.operandOfNew = operandOfNew;
		this.arguments = arguments;
	}

	public Function getFunctionDeclaration() {
		return functionDeclaration;
	}

	public void setFunctionDeclaration(Function functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

	public AbstractExpression getOperandOfNew() {
		return operandOfNew;
	}

	public String getClassName() {
		return QualifiedNameExtractor.getQualifiedName(operandOfNew
				.getExpression());
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

	public void setOperandOfNew(AbstractExpression operandOfNew) {
		this.operandOfNew = operandOfNew;
	}

	public void setArguments(List<AbstractExpression> arguments) {
		this.arguments = arguments;
	}
}
