package ca.concordia.javascript.analysis.util;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ConditionalExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NullTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.PostfixExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;

public class IdentifierHelper {
	private static final Logger log = Logger.getLogger(IdentifierHelper.class.getName());

	public static AbstractIdentifier getIdentifier(ParseTree node) {
		return getIdentifier(node, null);
	}

	public static AbstractIdentifier getIdentifier(IdentifierToken token, AbstractIdentifier composite) {
		if (composite == null)
			return new PlainIdentifier(token);
		else
			return new CompositeIdentifier(token, composite);
	}

	public static AbstractIdentifier getIdentifier(Token token) {
		return new PlainIdentifier(token);

	}

	public static AbstractIdentifier getIdentifier(ParseTree node, AbstractIdentifier composite) {
		AbstractIdentifier identifier;
		if (node instanceof LiteralExpressionTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof IdentifierExpressionTree) {
			if (composite == null)
				return new PlainIdentifier(node);
			else
				return composite;
		} else if (node instanceof ThisExpressionTree) {
			if (composite == null)
				return new PlainIdentifier(node);
			else
				return composite;
		} else if (node instanceof NewExpressionTree) {
			return getIdentifier(node.asNewExpression().operand);
		} else if (node instanceof MemberExpressionTree) {
			if (composite == null)
				identifier = new PlainIdentifier(node);
			else
				identifier = composite;
			return getIdentifier(node.asMemberExpression().operand, new CompositeIdentifier(node.asMemberExpression().operand, identifier));
		} else if (node instanceof MemberLookupExpressionTree) {
			if (composite == null)
				identifier = new PlainIdentifier(node);
			else
				identifier = composite;
			return getIdentifier(node.asMemberLookupExpression().operand, new CompositeIdentifier(node.asMemberLookupExpression().operand, identifier));
		} else if (node instanceof ParenExpressionTree) {
			identifier = new PlainIdentifier(node);
			return identifier;
		} else if (node instanceof CallExpressionTree) {
			return getIdentifier(node.asCallExpression().operand);
		} else if (node instanceof FunctionDeclarationTree) {
			return new PlainIdentifier(node.asFunctionDeclaration());
		} else if (node instanceof ArrayLiteralExpressionTree) {
			if (composite == null)
				return new PlainIdentifier(node);
			else
				return composite;
		} else if (node instanceof BinaryOperatorTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof ConditionalExpressionTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof ObjectLiteralExpressionTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof UnaryExpressionTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof PostfixExpressionTree) {
			return new PlainIdentifier(node);
		} else if (node instanceof NullTree) {
			return new PlainIdentifier(node);
		} else {
			throw new UnsupportedOperationException(node.getClass() + " is not supported as an identifier");
		}
	}

	public static boolean isCompositeIdentifier(AbstractIdentifier identifier) {
		if (identifier instanceof CompositeIdentifier)
			return true;
		else
			return false;
	}

	public static AbstractIdentifier findLValue(AbstractStatement statement, ParseTree rightValue) {
		if (statement.getStatement() instanceof ExpressionStatementTree) {
			ExpressionStatementTree epxressionStatement = statement.getStatement().asExpressionStatement();
			if (epxressionStatement.expression instanceof BinaryOperatorTree)
				return getIdentifier(epxressionStatement.expression.asBinaryOperator().left);
			else if (epxressionStatement.expression instanceof CallExpressionTree)
				return getIdentifier(epxressionStatement.expression.asCallExpression().operand);
		} else if (statement.getStatement() instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = statement.getStatement().asVariableStatement();
			VariableDeclarationListTree variableDeclarationListTree = variableStatement.declarations;
			for (VariableDeclarationTree variableDeclaration : variableDeclarationListTree.declarations)
				if (variableDeclaration.initializer != null && variableDeclaration.initializer.equals(rightValue))
					return getIdentifier(variableDeclaration.lvalue);
		}
		return null;
	}

	public static AbstractIdentifier removePart(AbstractIdentifier identifier, String partToRemove) {
		//	return identifier;
		return removePart(null, identifier, partToRemove);
	}

	private static AbstractIdentifier removePart(AbstractIdentifier newIdentifier, AbstractIdentifier identifier, String partToRemove) {
		if (identifier instanceof PlainIdentifier)
			if (newIdentifier == null)
				return identifier;
			else {
				if (newIdentifier instanceof PlainIdentifier)
					return new CompositeIdentifier(newIdentifier, identifier);
				return new CompositeIdentifier(newIdentifier.asCompositeIdentifier().getLeftPart(), new CompositeIdentifier(newIdentifier.asCompositeIdentifier().getMostRightPart(), identifier));
			}

		CompositeIdentifier composite = identifier.asCompositeIdentifier();
		if (composite.getMostLeftPart().toString().equals(partToRemove))
			return new CompositeIdentifier(newIdentifier, composite.getRightPart());
		if (composite.getMostRightPart().toString().equals(partToRemove))
			return new CompositeIdentifier(newIdentifier, composite.getLeftPart());

		if (newIdentifier == null)
			newIdentifier = composite.getMostLeftPart();
		else
			newIdentifier = new CompositeIdentifier(newIdentifier, composite.getMostLeftPart());

		newIdentifier = removePart(newIdentifier, composite.getRightPart(), partToRemove);
		return newIdentifier;
	}
}
