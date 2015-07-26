package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.DebugHelper;

public class ObjectCreation extends Creation {
	static Logger log = Logger.getLogger(ObjectCreation.class.getName());
	private AbstractFunctionFragment statement;
	private NewExpressionTree newExpressionTree;
	// In one scope we can have two functions with exact same names. but the
	// last one is executed in run-time because the last one is redeclared
	private FunctionDeclaration classDeclaration;
	private AbstractExpression operandOfNew;
	private List<AbstractExpression> arguments;
	private boolean isClassDeclarationPredefined = false;

	public ObjectCreation(NewExpressionTree newExpressionTree,
			AbstractFunctionFragment statement) {
		this.newExpressionTree = newExpressionTree;
		this.statement = statement;
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
			AbstractFunctionFragment statement,
			AbstractExpression operandOfNew, List<AbstractExpression> arguments) {
		this.newExpressionTree = newExpressionTree;
		// this.statement=statement;
		this.operandOfNew = operandOfNew;
		this.arguments = arguments;
	}

	public FunctionDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(FunctionDeclaration functionDeclaration) {
		classDeclaration = functionDeclaration;
	}

	public AbstractExpression getOperandOfNew() {
		return operandOfNew;
	}

	public String getClassName() {
		AbstractIdentifier identifier = IdentifierHelper
				.getIdentifier(operandOfNew.getExpression());
		return Strings.isNullOrEmpty(identifier.toString()) ? "<Anonymous>"
				: identifier.toString();
	}

	public AbstractIdentifier getIdentifier() {
		return IdentifierHelper.getIdentifier(operandOfNew.getExpression());
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

	public String toString() {
		return DebugHelper.extract(newExpressionTree);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ObjectCreation) {
			ObjectCreation toCompare = (ObjectCreation) other;
			return Objects.equals(this.newExpressionTree,
					toCompare.newExpressionTree);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(newExpressionTree);
	}

	public AbstractFunctionFragment getStatement() {
		return statement;
	}

	public void setStatement(AbstractFunctionFragment statement) {
		this.statement = statement;
	}

	public boolean isClassDeclarationPredefined() {
		return isClassDeclarationPredefined;
	}

	public void setClassDeclarationPredefined(
			boolean isClassDeclarationPredefined) {
		this.isClassDeclarationPredefined = isClassDeclarationPredefined;
	}
}
