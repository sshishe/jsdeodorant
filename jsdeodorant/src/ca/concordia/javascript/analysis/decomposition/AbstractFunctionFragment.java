package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTreeType;

import ca.concordia.javascript.analysis.abstraction.ArrayCreation;
import ca.concordia.javascript.analysis.abstraction.Creation;
import ca.concordia.javascript.analysis.abstraction.FunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.GlobalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.LocalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
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

	protected void processArrayCreations(List<ParseTree> arrayCreations) {
		for (ParseTree arrayCreation : arrayCreations) {
			if (arrayCreation instanceof ArrayLiteralExpressionTree) {
				ArrayCreation creationInsances = new ArrayCreation();
				// TODO set necessary properties
				addCreation(creationInsances);
			} else if (arrayCreation instanceof NewExpressionTree) {
				NewExpressionTree newArrayCreationExpression = (NewExpressionTree) arrayCreation;
				IdentifierExpressionTree identifierExpression = (IdentifierExpressionTree) newArrayCreationExpression.operand;
				if (identifierExpression.identifierToken.value
						.equals(ReservedIdentifierToken.Array.toString())) {
					ArrayCreation creationInsances = new ArrayCreation();
					// TODO set necessary properties
					addCreation(creationInsances);
				}
			}
		}
	}

	protected void processObjectCreations(List<ParseTree> objectCreations) {
		for (ParseTree arrayCreation : objectCreations) {
			if (arrayCreation instanceof ObjectLiteralExpressionTree) {
				ArrayCreation creationInsances = new ArrayCreation();
				// TODO set necessary properties
				addCreation(creationInsances);
			} else if (arrayCreation instanceof NewExpressionTree) {
				NewExpressionTree newArrayCreationExpression = (NewExpressionTree) arrayCreation;
				IdentifierExpressionTree identifierExpression = (IdentifierExpressionTree) newArrayCreationExpression.operand;
				if (!ReservedIdentifierToken
						.contains(identifierExpression.identifierToken.value)) {
					ObjectCreation creationInsances = new ObjectCreation();
					// TODO set necessary properties
					addCreation(creationInsances);
				}
			}
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
