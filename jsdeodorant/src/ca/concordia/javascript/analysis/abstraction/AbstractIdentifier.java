package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.util.IdentifierHelper;

import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.LiteralToken;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ConditionalExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;

public abstract class AbstractIdentifier {
	protected String identifierName;
	private ParseTree node;
	protected Token token;

	public AbstractIdentifier(Token token) {
		if (token instanceof IdentifierToken) {
			identifierName = token.asIdentifier().value;
		} else if (token instanceof LiteralToken) {
			identifierName = token.asLiteral().value;
		}
	}

	public AbstractIdentifier(ParseTree node) {
		this.node = node;
		this.identifierName = extractIdentifierName(node);
	}

	private String extractIdentifierName(ParseTree currentNode) {
		if (currentNode instanceof LiteralExpressionTree) {
			return currentNode.asLiteralExpression().literalToken.toString().replace("\"", "");
		} else if (currentNode instanceof IdentifierExpressionTree) {
			return currentNode.asIdentifierExpression().identifierToken.value;
		} else if (currentNode instanceof ThisExpressionTree) {
			return "this";
		} else if (currentNode instanceof MemberExpressionTree) {
			return currentNode.asMemberExpression().memberName.value;
		} else if (currentNode instanceof MemberLookupExpressionTree) {
			return extractIdentifierName(currentNode.asMemberLookupExpression().memberExpression);
		} else if (currentNode instanceof ParenExpressionTree) {
			return extractIdentifierName(currentNode.asParenExpression().expression);
		} else if (currentNode instanceof CallExpressionTree) {
			return extractIdentifierName(currentNode.asCallExpression().operand);
		} else if (currentNode instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclaration = currentNode.asFunctionDeclaration();
			if (functionDeclaration.name != null)
				return functionDeclaration.name.value;
		} else if (currentNode instanceof ArrayLiteralExpressionTree) {
			ArrayLiteralExpressionTree arrayLiteralExpression = currentNode.asArrayLiteralExpression();
			if (arrayLiteralExpression.elements.isEmpty())
				return "[]";
			else {
				StringBuilder sb = new StringBuilder("[");
				for (ParseTree element : arrayLiteralExpression.elements) {
					sb.append(IdentifierHelper.getIdentifier(element).identifierName);
					if (arrayLiteralExpression.elements.indexOf(element) != arrayLiteralExpression.elements.size() - 1)
						sb.append(',');
				}
				return sb.append(']').toString();
			}
		} else if (currentNode instanceof BinaryOperatorTree) {
			BinaryOperatorTree binaryOperator = currentNode.asBinaryOperator();
			return extractIdentifierName(binaryOperator.left) + " " + binaryOperator.operator + " " + extractIdentifierName(binaryOperator.right);
		} else if (currentNode instanceof ConditionalExpressionTree) {
			ConditionalExpressionTree conditionalExpression = currentNode.asConditionalExpression();
			return extractIdentifierName(conditionalExpression.condition) + " ? " + extractIdentifierName(conditionalExpression.left) + " : " + extractIdentifierName(conditionalExpression.right);
		} else if (currentNode instanceof UnaryExpressionTree) {
			UnaryExpressionTree unaryExpression = currentNode.asUnaryExpression();
			return unaryExpression.operator + extractIdentifierName(unaryExpression.operand);
		} else if (currentNode instanceof NewExpressionTree) {
			NewExpressionTree newExpression = currentNode.asNewExpression();
			return "new " + extractIdentifierName(newExpression.operand) + "(" + extractIdentifierName(newExpression.arguments) + ")";
		} else if (currentNode instanceof ArgumentListTree) {
			StringBuilder arguments = new StringBuilder("");
			for (ParseTree argument : currentNode.asArgumentList().arguments) {
				arguments.append(extractIdentifierName(argument));
				if (currentNode.asArgumentList().arguments.indexOf(argument) != currentNode.asArgumentList().arguments.size() - 1)
					arguments.append(",");
			}
			return arguments.toString();
		}
		if (currentNode == null)
			return "";
		return "<" + currentNode.getClass() + ">";
		//throw new UnsupportedOperationException("Node type is not supported: " + currentNode.getClass());
	}

	public ParseTree getNode() {
		return node;
	}

	public void setNode(ParseTree node) {
		this.node = node;
	}

	public AbstractIdentifier asPlainIdentifier() {
		return (AbstractIdentifier) this;
	}

	public CompositeIdentifier asCompositeIdentifier() {
		return (CompositeIdentifier) this;
	}

	public String getIdentifierName() {
		return identifierName;
	}

	public void setIdentifierName(String identifierName) {
		this.identifierName = identifierName;
	}
}
