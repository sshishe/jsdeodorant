package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.ArrayLiteralCreation;
import ca.concordia.jsdeodorant.analysis.abstraction.Creation;
import ca.concordia.jsdeodorant.analysis.abstraction.FunctionInvocation;
import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.abstraction.VariableDeclaration;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public abstract class AbstractFunctionFragment {
	private static final Logger log = Logger.getLogger(AbstractFunctionFragment.class.getName());
	private SourceContainer parent;
	private List<Creation> creationList;
	private List<FunctionInvocation> functionInvocationList;
	private List<FunctionDeclarationExpression> functionDeclarationExpressionList;
	private List<ObjectLiteralExpression> objectLiteralExpressionList;
	private List<AbstractExpression> assignmentExpressionList;
	private List<VariableDeclaration> variableDeclarationList;

	protected AbstractFunctionFragment(SourceContainer parent) {
		this.parent = parent;
		creationList = new ArrayList<>();
		functionInvocationList = new ArrayList<>();
		functionDeclarationExpressionList = new ArrayList<>();
		objectLiteralExpressionList = new ArrayList<>();
		variableDeclarationList = new ArrayList<>();
		assignmentExpressionList = new ArrayList<>();
	}

	protected void processFunctionInvocations(List<ParseTree> functionInvocations) {
		for (ParseTree functionInvocation : functionInvocations) {
			CallExpressionTree callExpression = functionInvocation.asCallExpression();
			List<AbstractExpression> arguments = new ArrayList<>();
			if (callExpression.arguments != null) {
				for (ParseTree argument : callExpression.arguments.arguments) {
					arguments.add(new AbstractExpression(argument));
				}
			}
			AbstractIdentifier identifier = IdentifierHelper.getIdentifier(callExpression.operand);

			FunctionInvocation functionInvocationObject = new FunctionInvocation(callExpression, identifier, new AbstractExpression(callExpression.operand, parent, false), arguments);
			addFunctionInvocation(functionInvocationObject);
		}
	}

	protected void addFunctionInvocation(FunctionInvocation functionInvocation) {
		functionInvocationList.add(functionInvocation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			if (!compositeContainsFunctionInvocation(functionInvocation, compositeStatement))
				compositeStatement.addFunctionInvocation(functionInvocation);
		}
	}

	private boolean compositeContainsFunctionInvocation(FunctionInvocation functionInvocation, CompositeStatement composite) {
		for (FunctionInvocation invocation : composite.getFunctionInvocationList())
			if (functionInvocation.equals(invocation))
				return true;
		return false;
	}

	protected void processVariableDeclarations(List<ParseTree> variableDeclarations) {
		for (ParseTree variableDeclarationTree : variableDeclarations) {
			VariableDeclarationTree variableDeclaration = variableDeclarationTree.asVariableDeclaration();

			AbstractIdentifier identifier = IdentifierHelper.getIdentifier(variableDeclaration.lvalue);

			AbstractExpression initializer = new AbstractExpression(variableDeclaration.initializer);

			VariableDeclaration variableDeclarationObject = new VariableDeclaration(variableDeclaration, identifier, initializer);
			addVariableDeclaration(variableDeclarationObject);
		}
	}

	protected void addVariableDeclaration(VariableDeclaration variableDeclaration) {
		variableDeclarationList.add(variableDeclaration);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			if (!compositeContainsVariableDeclaration(variableDeclaration, compositeStatement))
				compositeStatement.addVariableDeclaration(variableDeclaration);
		}
	}

	private boolean compositeContainsVariableDeclaration(VariableDeclaration variableDeclarationObject, CompositeStatement composite) {
		for (VariableDeclaration variableDeclaration : composite.getVariableDeclarationList())
			if (variableDeclarationObject.equals(variableDeclaration))
				return true;
		return false;
	}

	public void addFunctionDeclarationExpression(FunctionDeclarationExpression functionDeclaration) {
		functionDeclarationExpressionList.add(functionDeclaration);
	}

	public void addObjectLiteralExpression(ObjectLiteralExpression objectLiteral) {
		objectLiteralExpressionList.add(objectLiteral);
	}

	public void addAssignmentExpression(AbstractExpression assignment) {
		assignmentExpressionList.add(assignment);
	}

	protected void processNewExpressions(List<ParseTree> newExpressions) {
		for (ParseTree expression : newExpressions) {
			NewExpressionTree newExpression = expression.asNewExpression();
			ObjectCreation objectCreation = new ObjectCreation(newExpression, this);
			AbstractExpression operandOfNew = null;
			// TODO Redundant checks, it can be just assigned as ParseTree node
			if (newExpression.operand instanceof IdentifierExpressionTree)
				operandOfNew = new AbstractExpression(newExpression.operand.asIdentifierExpression());
			else if (newExpression.operand instanceof MemberExpressionTree)
				operandOfNew = new AbstractExpression(newExpression.operand.asMemberExpression());
			else if (newExpression.operand instanceof ParenExpressionTree)
				operandOfNew = new AbstractExpression(newExpression.operand.asParenExpression());
			else if (newExpression.operand instanceof MemberLookupExpressionTree)
				operandOfNew = new AbstractExpression(newExpression.operand.asMemberLookupExpression());
			else if (newExpression.operand instanceof ThisExpressionTree) {
				operandOfNew = new AbstractExpression(newExpression.operand.asThisExpression());
			} else if (newExpression.operand instanceof FunctionDeclarationTree) {
				operandOfNew = new AbstractExpression(newExpression.operand.asFunctionDeclaration());
			} else
				log.warn("The missing type that we should handle for the operand of New expression is:" + newExpression.operand.getClass() + " " + newExpression.location);
			ArgumentListTree argumentList = newExpression.arguments;
			List<AbstractExpression> arguments = new ArrayList<>();
			if (newExpression.arguments != null)
				for (ParseTree argument : argumentList.arguments) {
					arguments.add(new AbstractExpression(argument));
				}
			objectCreation.setNewExpressionTree(newExpression);
			objectCreation.setOperandOfNew(operandOfNew);
			objectCreation.setArguments(arguments);
			addCreation(objectCreation);
		}
	}

	protected void processArrayLiteralExpressions(List<ParseTree> arrayLiteralExpressions) {
		for (ParseTree expression : arrayLiteralExpressions) {
			ArrayLiteralExpressionTree arrayLiteral = (ArrayLiteralExpressionTree) expression;
			ImmutableList<ParseTree> elements = arrayLiteral.elements;
			List<AbstractExpression> arguments = new ArrayList<>();
			for (ParseTree argument : elements) {
				arguments.add(new AbstractExpression(argument));
			}
			ArrayLiteralCreation arrayLiteralCreation = new ArrayLiteralCreation(arguments);
			addCreation(arrayLiteralCreation);
		}
	}

	protected void processAssignmentExpressions(List<ParseTree> binaryOperatorExpressions) {
		for (ParseTree expression : binaryOperatorExpressions) {
			if (expression instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = expression.asBinaryOperator();
				if (binaryOperatorTree.operator.toString().equals("="))
					addAssignmentExpression(new AbstractExpression(expression));
			}
		}
	}

	protected void addCreation(Creation creation) {
		creationList.add(creation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			if (!compositeContainsCreation(creation, compositeStatement))
				compositeStatement.addCreation(creation);
		}
	}

	private boolean compositeContainsCreation(Creation creation, CompositeStatement composite) {
		for (Creation existingCreation : composite.getCreations())
			if (creation.equals(existingCreation))
				return true;
		return false;
	}

	public SourceContainer getParent() {
		return this.parent;
	}

	public FunctionDeclaration getParentFunction() {
		if (this.parent == null)
			return null;

		return findParentFunction(this.parent);
	}

	private FunctionDeclaration findParentFunction(SourceContainer element) {
		if (element instanceof FunctionDeclaration)
			return (FunctionDeclaration) element;
		if (element instanceof AbstractStatement) {
			return findParentFunction(((AbstractStatement) element).getParent());
		}
		if (element instanceof ObjectLiteralExpression) {
			return findParentFunction(((AbstractExpression) element).getParent());
		}
		return null;
	}

	public List<Creation> getCreations() {
		return creationList;
	}

	public List<ObjectCreation> getObjectCreations() {
		List<ObjectCreation> objectCreations = new ArrayList<>();
		for (Creation creation : creationList) {
			if (creation instanceof ObjectCreation)
				objectCreations.add((ObjectCreation) creation);
		}
		return objectCreations;
	}

	public List<FunctionDeclarationExpression> getFunctionDeclarationExpressionList() {
		return functionDeclarationExpressionList;
	}

	public List<ObjectLiteralExpression> getObjectLiteralExpressionList() {
		return objectLiteralExpressionList;
	}

	public List<FunctionInvocation> getFunctionInvocationList() {
		return functionInvocationList;
	}

	public List<VariableDeclaration> getVariableDeclarationList() {
		return variableDeclarationList;
	}

	public List<AbstractExpression> getAssignmentExpressionList() {
		return assignmentExpressionList;
	}

	protected List<AbstractStatement> getReturnStatementListExtracted(List<AbstractStatement> statementsList) {
		List<AbstractStatement> statements = new ArrayList<>();
		for (AbstractStatement abstractStatement : statementsList) {
			if (abstractStatement instanceof CompositeStatement)
				for (AbstractStatement statement : ((CompositeStatement) abstractStatement).getStatements()) {
					if (statement.getType().equals(StatementType.RETURN)) {
						statements.add(statement);
					}
				}
		}
		return statements;
	}
}
