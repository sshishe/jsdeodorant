package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.DebugHelper;

public class FunctionInvocation {
	private CallExpressionTree callExpressionTree;
	private AbstractIdentifier member;
	private AbstractExpression operand;
	private List<AbstractExpression> arguments;
	private FunctionDeclaration functionDeclaration;

	public FunctionInvocation(CallExpressionTree callExpressionTree,
			AbstractIdentifier member, AbstractExpression operand,
			List<AbstractExpression> arguments) {
		this.callExpressionTree = callExpressionTree;
		this.member = member;
		this.operand = operand;
		this.arguments = arguments;
	}

	public String getMemberName() {
		return member.identifierName;
	}

	public void setMemberName(AbstractIdentifier member) {
		this.member = member;
	}

	public AbstractExpression getOperand() {
		return operand;
	}

	public void setOperand(AbstractExpression operand) {
		this.operand = operand;
	}

	public List<AbstractExpression> getArguments() {
		return arguments;
	}

	public void setArguments(List<AbstractExpression> arguments) {
		this.arguments = arguments;
	}

	public String toString() {
		return DebugHelper.extract(callExpressionTree);
	}

	public CallExpressionTree getCallExpressionTree() {
		return callExpressionTree;
	}

	public void setCallExpressionTree(CallExpressionTree callExpressionTree) {
		this.callExpressionTree = callExpressionTree;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FunctionInvocation) {
			FunctionInvocation toCompare = (FunctionInvocation) other;
			return Objects.deepEquals(this.callExpressionTree,
					toCompare.callExpressionTree);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(callExpressionTree);
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}

	public void setFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}
}
