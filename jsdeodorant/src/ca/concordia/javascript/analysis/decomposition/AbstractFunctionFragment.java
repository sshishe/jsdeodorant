package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.LiteralToken;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;

import ca.concordia.javascript.analysis.abstraction.AnonymousFunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.ArrayLiteralCreation;
import ca.concordia.javascript.analysis.abstraction.ClassDeclarationType;
import ca.concordia.javascript.analysis.abstraction.Creation;
import ca.concordia.javascript.analysis.abstraction.Function;
import ca.concordia.javascript.analysis.abstraction.FunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.ObjectLiteralCreation;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.Function.Kind;
import ca.concordia.javascript.analysis.util.QualifiedNameExtractor;

public abstract class AbstractFunctionFragment {
	private static final Logger log = Logger
			.getLogger(AbstractFunctionFragment.class.getName());
	private SourceContainer parent;
	private List<Creation> creationList;
	private List<FunctionInvocation> functionInvocationList;
	private List<FunctionDeclaration> functionDeclarationList;
	private List<AnonymousFunctionDeclaration> anonymousFunctionDeclarationList;

	protected AbstractFunctionFragment(SourceContainer parent) {
		this.parent = parent;
		creationList = new ArrayList<>();
		functionInvocationList = new ArrayList<>();
		functionDeclarationList = new ArrayList<>();
		anonymousFunctionDeclarationList = new ArrayList<>();
	}

	/*public static FunctionDeclaration processFunctionDeclaration(
			FunctionDeclarationTree functionDeclarationTree) {
		FunctionDeclaration functionDeclaration = new FunctionDeclaration();

		functionDeclaration.setFunctionDeclarationTree(functionDeclarationTree);

		if (functionDeclarationTree.name != null)
			functionDeclaration.setName(functionDeclarationTree.name.value);

		functionDeclaration.setKind(Kind.valueOf(functionDeclarationTree.kind
				.toString()));

		if (functionDeclarationTree.formalParameterList != null) {
			FormalParameterListTree formalParametersList = functionDeclarationTree.formalParameterList
					.asFormalParameterList();
			for (ParseTree parameter : formalParametersList.parameters)
				functionDeclaration.addParameter(new AbstractExpression(
						parameter));
		}

		ParseTree functionBodyTree = functionDeclarationTree.functionBody;

		if (functionBodyTree instanceof BlockTree) {
			BlockTree blockTree = functionBodyTree.asBlock();
			FunctionBody functionBody = new FunctionBody(blockTree);
			functionDeclaration.setBody(functionBody);
		}

		// If the body is not BlockTree it will be an expression
		else {
			log.warn("Unsupported expression");
		}

		return functionDeclaration;
	}*/

	protected void processFunctionInvocations(
			List<ParseTree> functionInvocations) {
		for (ParseTree functionInvocation : functionInvocations) {
			CallExpressionTree callExpression = (CallExpressionTree) functionInvocation;
			List<AbstractExpression> arguments = new ArrayList<>();
			if (callExpression.arguments != null) {
				for (ParseTree argument : callExpression.arguments.arguments) {
					arguments.add(new AbstractExpression(argument));
				}
			}
			String functionName = QualifiedNameExtractor.getQualifiedName(
					callExpression.operand).toString();

			FunctionInvocation functionInvocationObject = new FunctionInvocation(callExpression,
					functionName, new AbstractExpression(
							callExpression.operand), arguments);
			addFunctionInvocation(functionInvocationObject);
		}
	}

	protected void addFunctionInvocation(FunctionInvocation functionInvocation) {
		functionInvocationList.add(functionInvocation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement.addFunctionInvocation(functionInvocation);
		}
	}

	/*protected void processFunctionDeclarations(
			List<ParseTree> functionDeclarations) {
		for (ParseTree functionDeclaration : functionDeclarations) {
			FunctionDeclarationTree functionDeclarationTree = functionDeclaration
					.asFunctionDeclaration();
			// if (functionDeclarationTree.kind ==
			// FunctionDeclarationTree.Kind.DECLARATION)
			if (functionDeclarationTree.name != null)
				addFunctionDeclaration(processFunctionDeclaration(
						functionDeclarationTree));
		}
	}

	protected void processAnonymousFunctionDeclarations(
			List<ParseTree> anonymousFunctionDeclarations) {
		for (ParseTree anonymousFunctionDeclaration : anonymousFunctionDeclarations)
			if (anonymousFunctionDeclaration instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = anonymousFunctionDeclaration
						.asBinaryOperator();
				if (binaryOperatorTree.right instanceof FunctionDeclarationTree) {
					FunctionDeclarationTree functionDeclarationTree = binaryOperatorTree.right
							.asFunctionDeclaration();
					AnonymousFunctionDeclaration anonymousFunctionDeclarationObject = new AnonymousFunctionDeclaration(
							new AbstractExpression(binaryOperatorTree.left), processFunctionDeclaration(
									functionDeclarationTree));
					anonymousFunctionDeclarationObject
							.setFunctionDeclarationTree(functionDeclarationTree);
					addAnonymousFunctionDeclaration(anonymousFunctionDeclarationObject);
				}
			} else if (anonymousFunctionDeclaration instanceof VariableDeclarationTree) {

				VariableDeclarationTree variableDeclarationTree = anonymousFunctionDeclaration
						.asVariableDeclaration();
				if (variableDeclarationTree.initializer instanceof FunctionDeclarationTree) {
					FunctionDeclarationTree functionDeclarationTree = variableDeclarationTree.initializer
							.asFunctionDeclaration();
					AnonymousFunctionDeclaration anonymousFunctionDeclarationObject = new AnonymousFunctionDeclaration(
							new AbstractExpression(
									variableDeclarationTree.lvalue),
							processFunctionDeclaration(functionDeclarationTree));
					anonymousFunctionDeclarationObject
							.setFunctionDeclarationTree(functionDeclarationTree);
					addAnonymousFunctionDeclaration(anonymousFunctionDeclarationObject);
				}

			}
	}*/

	public void addAnonymousFunctionDeclaration(
			AnonymousFunctionDeclaration anonymousFunctionDeclarationObject) {
		anonymousFunctionDeclarationList
				.add(anonymousFunctionDeclarationObject);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement
					.addAnonymousFunctionDeclaration(anonymousFunctionDeclarationObject);
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
			ObjectCreation objectCreation = new ObjectCreation(newExpression);
			AbstractExpression operandOfNew = null;

			if (newExpression.operand instanceof IdentifierExpressionTree)
				operandOfNew = new AbstractExpression(
						newExpression.operand.asIdentifierExpression());

			// TODO support MemberLookupExpressionTrees i.e: var xhr = new
			// goog.global['XMLHttpRequest']();
			// and also support for ParenExpressionTree i.e: var col = new
			// (Backbone.Collection.extend({ model: Model }))();
			else if (newExpression.operand instanceof MemberExpressionTree)
				// TODO check if we need to find the type of memberName i.e. the
				// type of "x"
				// in x.Child("Something") where x could be an instance of
				// another class
				operandOfNew = new AbstractExpression(
						newExpression.operand.asMemberExpression());

			else if (newExpression.operand instanceof ParenExpressionTree)
				operandOfNew = new AbstractExpression(
						newExpression.operand.asParenExpression());
			else if (newExpression.operand instanceof MemberLookupExpressionTree)
				operandOfNew = new AbstractExpression(
						newExpression.operand.asMemberLookupExpression());
			//TODO handle the following case in StatementProcessor
			/*else if (newExpression.operand instanceof FunctionDeclarationTree) {
				// i.e new function() {};
				operandOfNew = new AbstractExpression(
						newExpression.operand.asFunctionDeclaration());
				Function anonymousFunctionDeclaration = new AnonymousFunctionDeclaration(
						null, processFunctionDeclaration(
								newExpression.operand.asFunctionDeclaration()));
				anonymousFunctionDeclaration
						.setFunctionDeclarationTree(newExpression.operand
								.asFunctionDeclaration());
				objectCreation.setClassDeclaration(
						ClassDeclarationType.ANONYMOUS,
						anonymousFunctionDeclaration);

			}*/ else if (newExpression.operand instanceof ThisExpressionTree) {
				operandOfNew = new AbstractExpression(
						newExpression.operand.asThisExpression());
			} else
				log.warn("The missing type that we should handle for the operand of New expression is:"
						+ newExpression.operand.getClass()
						+ " "
						+ newExpression.location);

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

	protected void processObjectLiteralExpressions(
			List<ParseTree> objectLiteralExpressions) {
		for (ParseTree expression : objectLiteralExpressions) {
			ObjectLiteralExpressionTree objectLiteral = (ObjectLiteralExpressionTree) expression;
			ImmutableList<ParseTree> nameAndValues = objectLiteral.propertyNameAndValues;
			Map<String, AbstractExpression> propertyMap = new LinkedHashMap<>();
			for (ParseTree argument : nameAndValues) {
				if (argument instanceof PropertyNameAssignmentTree) {
					// TODO handle nested properties
					PropertyNameAssignmentTree propertyNameAssignment = (PropertyNameAssignmentTree) argument;
					Token token = propertyNameAssignment.name;
					String name = null;
					if (token instanceof IdentifierToken) {
						name = token.asIdentifier().value;
					} else if (token instanceof LiteralToken) {
						name = token.asLiteral().value;
					}
					ParseTree value = propertyNameAssignment.value;
					propertyMap.put(name, new AbstractExpression(value));
				}
			}
			ObjectLiteralCreation objectLiteralCreation = new ObjectLiteralCreation(
					propertyMap);
			addCreation(objectLiteralCreation);
		}
	}

	protected void processArrayLiteralExpressions(
			List<ParseTree> arrayLiteralExpressions) {
		for (ParseTree expression : arrayLiteralExpressions) {
			ArrayLiteralExpressionTree arrayLiteral = (ArrayLiteralExpressionTree) expression;
			ImmutableList<ParseTree> elements = arrayLiteral.elements;
			List<AbstractExpression> arguments = new ArrayList<>();
			for (ParseTree argument : elements) {
				arguments.add(new AbstractExpression(argument));
			}
			ArrayLiteralCreation arrayLiteralCreation = new ArrayLiteralCreation(
					arguments);
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

	public SourceContainer getParent() {
		return this.parent;
	}

	public List<Creation> getCreations() {
		return creationList;
	}

	public List<FunctionDeclaration> getFuntionDeclarations() {
		return functionDeclarationList;
	}

	public List<AnonymousFunctionDeclaration> getAnonymousFuntionDeclarations() {
		return anonymousFunctionDeclarationList;
	}

	public List<FunctionInvocation> getFunctionInvocationList() {
		return functionInvocationList;
	}
}
