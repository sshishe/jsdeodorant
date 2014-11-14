package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.ArrayCreation;
import ca.concordia.javascript.analysis.abstraction.ArrayLiteralCreation;
import ca.concordia.javascript.analysis.abstraction.Creation;
import ca.concordia.javascript.analysis.abstraction.FunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.GlobalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.LocalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.ObjectLiteralCreation;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;

public abstract class AbstractFunctionFragment {
	private SourceContainer parent;
	private List<Creation> creationList;
	private List<FunctionInvocation> functionInvocationList;
	private List<FunctionDeclaration> functionDeclarationList;
	private List<LocalVariableDeclaration> localVariableDeclarationList;
	private List<GlobalVariableDeclaration> globalVariableDeclarationList;

	protected AbstractFunctionFragment(SourceContainer parent) {
		this.parent = parent;
		creationList = new ArrayList<>();
		functionInvocationList = new ArrayList<>();
		functionDeclarationList = new ArrayList<>();
		localVariableDeclarationList = new ArrayList<>();
		globalVariableDeclarationList = new ArrayList<>();
	}

	public SourceContainer getParent() {
		return this.parent;
	}

	protected void processFunctionInvocations(
			List<ParseTree> functionInvocations) {
		for (ParseTree functionInvocation : functionInvocations) {
			CallExpressionTree callExpression = (CallExpressionTree) functionInvocation;
			if (callExpression.operand instanceof MemberExpressionTree) {
				MemberExpressionTree operand = (MemberExpressionTree) callExpression.operand;

				List<AbstractExpression> arguments = new ArrayList<>();
				if (callExpression.arguments != null) {
					for (ParseTree argument : callExpression.arguments.arguments) {
						arguments.add(new AbstractExpression(argument));
					}
				}
				addFunctionInvocation(new FunctionInvocation(
						operand.memberName.value, new AbstractExpression(
								operand), arguments));
			}

		}
	}

	protected void addFunctionInvocation(FunctionInvocation functionInvocation) {
		functionInvocationList.add(functionInvocation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement.addFunctionInvocation(functionInvocation);
		}
	}

	protected void processFunctionDeclarations(
			List<ParseTree> functionDeclarations) {
		for (ParseTree functionDeclaration : functionDeclarations) {
			FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree) functionDeclaration;
			addFunctionDeclaration(processFunctionDeclaration(functionDeclarationTree));
		}
	}

	protected void addFunctionDeclaration(
			FunctionDeclaration functionDeclaration) {
		functionDeclarationList.add(functionDeclaration);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement.addFunctionDeclaration(functionDeclaration);
		}
	}

	protected void processNewExpressions(List<ParseTree> newExpressions) {
		for (ParseTree expression : newExpressions) {
			NewExpressionTree newExpression = (NewExpressionTree) expression;
			IdentifierExpressionTree identifierExpression = (IdentifierExpressionTree) newExpression.operand;
			String identifierTokenValue = identifierExpression.identifierToken.value;
			if (identifierTokenValue.equals(ReservedIdentifierToken.Array.toString())) {
				ArrayCreation arrayCreation = new ArrayCreation();
				// TODO set necessary properties
				addCreation(arrayCreation);
			}
			else if (!ReservedIdentifierToken.contains(identifierTokenValue)) {
				ObjectCreation objectCreation = new ObjectCreation(identifierTokenValue);
				// TODO set necessary properties
				addCreation(objectCreation);
			}
		}
	}

	protected void processObjectLiteralExpressions(List<ParseTree> objectLiteralExpressions) {
		for (ParseTree expression : objectLiteralExpressions) {
			ObjectLiteralExpressionTree objectLiteral = (ObjectLiteralExpressionTree) expression;
			ImmutableList<ParseTree> nameAndValues = objectLiteral.propertyNameAndValues;
			ObjectLiteralCreation objectLiteralCreation = new ObjectLiteralCreation();
			addCreation(objectLiteralCreation);
		}
	}

	protected void processArrayLiteralExpressions(List<ParseTree> arrayLiteralExpressions) {
		for (ParseTree expression : arrayLiteralExpressions) {
			ArrayLiteralExpressionTree arrayLiteral = (ArrayLiteralExpressionTree) expression;
			ImmutableList<ParseTree> elements = arrayLiteral.elements;
			ArrayLiteralCreation arrayLiteralCreation = new ArrayLiteralCreation();
			addCreation(arrayLiteralCreation);
		}
	}

	protected void addCreation(Creation creation) {
		creationList.add(creation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement.addCreation(creation);
		}
	}

	public static FunctionDeclaration processFunctionDeclaration(
			FunctionDeclarationTree functionDeclarationTree) {
		FunctionDeclaration functionDeclaration = new FunctionDeclaration();
		if (functionDeclarationTree.name != null)
			functionDeclaration.setName(functionDeclarationTree.name.value);

		ParseTree functionBodyTree = functionDeclarationTree.functionBody;

		if (functionBodyTree instanceof BlockTree) {
			BlockTree blockTree = functionBodyTree.asBlock();
			FunctionBody functionBody = new FunctionBody(blockTree);
			functionDeclaration.setBody(functionBody);
		}

		// If the body is not BlockTree it will be an expression
		else {

		}

		return functionDeclaration;
	}
}
