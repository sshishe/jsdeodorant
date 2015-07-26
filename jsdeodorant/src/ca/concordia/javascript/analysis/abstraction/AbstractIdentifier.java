package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.util.IdentifierHelper;

import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.LiteralToken;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;

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
		extractIdentifierName(node);
	}

	private void extractIdentifierName(ParseTree currentNode) {
		if (currentNode instanceof LiteralExpressionTree) {
			identifierName = currentNode.asLiteralExpression().literalToken.toString().replace("\"", "");
		} else if (currentNode instanceof IdentifierExpressionTree) {
			identifierName = currentNode.asIdentifierExpression().identifierToken.value;
		} else if (currentNode instanceof ThisExpressionTree) {
			identifierName = "this";
		} else if (currentNode instanceof NewExpressionTree) {

		} else if (currentNode instanceof MemberExpressionTree) {
			identifierName = currentNode.asMemberExpression().memberName.value;
		} else if (currentNode instanceof MemberLookupExpressionTree) {
			extractIdentifierName(node.asMemberLookupExpression().memberExpression);
		} else if (currentNode instanceof ParenExpressionTree) {

		} else if (currentNode instanceof CallExpressionTree) {

		} else if (currentNode instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclaration = currentNode.asFunctionDeclaration();
			if (functionDeclaration.name != null)
				identifierName = functionDeclaration.name.value;
		} else if (currentNode instanceof ArrayLiteralExpressionTree) {
			ArrayLiteralExpressionTree arrayLiteralExpression = currentNode.asArrayLiteralExpression();
			if (arrayLiteralExpression.elements.isEmpty())
				identifierName = "[]";
			else {
				StringBuilder sb = new StringBuilder("[");
				for (ParseTree element : arrayLiteralExpression.elements) {
					sb.append(IdentifierHelper.getIdentifier(element).identifierName);
					if (arrayLiteralExpression.elements.indexOf(element) != arrayLiteralExpression.elements.size() - 1)
						sb.append(',');
				}
				identifierName = sb.append(']').toString();
			}
		}

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
