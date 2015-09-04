package ca.concordia.javascript.analysis.util;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;

import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
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
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

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
			identifier = new PlainIdentifier(node);
			return new CompositeIdentifier(node.asNewExpression().operand, identifier);
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
			//return new CompositeIdentifier(node.asParenExpression().expression, identifier);
			return identifier;
		} else if (node instanceof CallExpressionTree) {
			if (composite == null)
				identifier = new PlainIdentifier(node);
			else
				identifier = composite;
			return new CompositeIdentifier(node.asCallExpression().operand, identifier);
		} else if (node instanceof FunctionDeclarationTree) {
			//return StatementProcessor.getFunctionDeclarationExpression(variableDeclarationTree, null);
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
		} else {
			throw new UnsupportedOperationException(node.getClass() + " is not supported as an identifier");
		}
		// return null;
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
}
