package ca.concordia.javascript.analysis.abstraction;

import java.util.Objects;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.DebugHelper;

import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;

public class VariableDeclaration {
	private VariableDeclarationTree variableDeclarationTree;
	private AbstractIdentifier identifier;
	private AbstractExpression operand;

	public VariableDeclaration(VariableDeclarationTree variableDeclarationTree,
			AbstractIdentifier identifier, AbstractExpression operand) {
		this.variableDeclarationTree = variableDeclarationTree;
		this.identifier = identifier;
		this.operand = operand;
	}

	public String toString() {
		return DebugHelper.extract(variableDeclarationTree);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof VariableDeclaration) {
			VariableDeclaration toCompare = (VariableDeclaration) other;
			return Objects.deepEquals(this.variableDeclarationTree,
					toCompare.variableDeclarationTree);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(variableDeclarationTree);
	}

	public VariableDeclarationTree getVariableDeclarationTree() {
		return variableDeclarationTree;
	}

	public void setVariableDeclarationTree(
			VariableDeclarationTree variableDeclarationTree) {
		this.variableDeclarationTree = variableDeclarationTree;
	}

	public AbstractIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(AbstractIdentifier identifier) {
		this.identifier = identifier;
	}

	public AbstractExpression getOperand() {
		return operand;
	}

	public void setOperand(AbstractExpression operand) {
		this.operand = operand;
	}
}
