package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.DebugHelper;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class FunctionInvocation {
	private AbstractIdentifier identifier;
	private CallExpressionTree callExpressionTree;
	private AbstractIdentifier member;
	private AbstractExpression operand;
	private List<AbstractExpression> arguments;
	private FunctionDeclaration functionDeclaration;
	private boolean isPredefined = false;
	private Module functionDeclarationModule;

	public FunctionInvocation(CallExpressionTree callExpressionTree, AbstractIdentifier member, AbstractExpression operand, List<AbstractExpression> arguments) {
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
			return Objects.deepEquals(this.callExpressionTree, toCompare.callExpressionTree);
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

	public void setFunctionDeclaration(FunctionDeclaration functionDeclaration, Module functionDeclarationModule) {
		this.functionDeclaration = functionDeclaration;
		this.functionDeclarationModule = functionDeclarationModule;

	}

	public boolean isPredefined() {
		return isPredefined;
	}

	public void setPredefinedState(boolean state) {
		this.isPredefined = state;
	}

	public AbstractIdentifier getIdentifier() {
		if (identifier == null)
			identifier = IdentifierHelper.getIdentifier(this.callExpressionTree);
		return identifier;
	}

	public String getPredefinedName() {
		if (getIdentifier() instanceof CompositeIdentifier)
			return getIdentifier().asCompositeIdentifier().getMostRightPart().toString();
		else
			return getIdentifier().toString();
	}

	public Module getFunctionDeclarationModule() {
		return functionDeclarationModule;
	}

	public void seFunctionDeclarationModule(Module definitionModule) {
		this.functionDeclarationModule = definitionModule;
	}
}
