package ca.concordia.javascript.analysis.util;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.QualifiedName;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;

public class QualifiedNameExtractor {
	private static final Logger log = Logger
			.getLogger(QualifiedNameExtractor.class.getName());

	// public static String getQualifiedNamee(ParseTree expression) {
	// if (expression instanceof LiteralExpressionTree)
	// return expression.asLiteralExpression().literalToken.toString()
	// .replace("\"", "");
	// else if (expression instanceof IdentifierExpressionTree)
	// return expression.asIdentifierExpression().identifierToken.value;
	// else if (expression instanceof ThisExpressionTree)
	// return "this";
	// else if (expression instanceof NewExpressionTree) {
	// return getQualifiedNamee(expression.asNewExpression().operand);
	// } else if (expression instanceof MemberExpressionTree)
	// return getQualifiedNamee(expression.asMemberExpression().operand)
	// + "." + expression.asMemberExpression().memberName.value;
	// else if (expression instanceof MemberLookupExpressionTree)
	// return getQualifiedNamee(expression.asMemberLookupExpression().operand)
	// + "."
	// +
	// getQualifiedNamee(expression.asMemberLookupExpression().memberExpression);
	// else if (expression instanceof ParenExpressionTree)
	// return getQualifiedNamee(expression.asParenExpression().expression);
	// else if (expression instanceof CallExpressionTree)
	// return getQualifiedNamee(expression.asCallExpression().operand);
	// else if (expression instanceof FunctionDeclarationTree) {
	// FunctionDeclarationTree functionDeclaration = expression
	// .asFunctionDeclaration();
	// if (functionDeclaration.name != null)
	// log.warn("Named function declaration as the right operand of new expression, is it correct?");
	// return "AnonymousFunction at:"
	// + expression.asFunctionDeclaration().location;
	// } else {
	// // log.warn(expression.getClass());
	// return "";
	// }
	// }

	public static QualifiedName getQualifiedName(ParseTree expression) {
		return getQualifiedName(expression, new QualifiedName());
	}

	public static QualifiedName getQualifiedName(ParseTree expression,
			QualifiedName qualifiedName) {
		if (expression instanceof LiteralExpressionTree) {
			qualifiedName.setName(expression.asLiteralExpression().literalToken
					.toString().replace("\"", ""));
			return qualifiedName.setNode(expression.asLiteralExpression());
		} else if (expression instanceof IdentifierExpressionTree) {
			qualifiedName
					.setName(expression.asIdentifierExpression().identifierToken.value);
			return qualifiedName.setNode(expression.asIdentifierExpression());
		} else if (expression instanceof ThisExpressionTree) {
			qualifiedName.setName("this");
			return qualifiedName.setNode(expression.asThisExpression());

		} else if (expression instanceof NewExpressionTree) {
			qualifiedName.setNode(expression.asNewExpression());
			return qualifiedName.setParent(getQualifiedName(
					expression.asNewExpression().operand,
					qualifiedName.createParent()));
		} else if (expression instanceof MemberExpressionTree) {
			qualifiedName.setNode(expression.asMemberExpression());
			qualifiedName
					.setName(expression.asMemberExpression().memberName.value);
			return qualifiedName.setParent(getQualifiedName(
					expression.asMemberExpression().operand,
					qualifiedName.createParent()));
		} else if (expression instanceof MemberLookupExpressionTree) {
			qualifiedName
					.setNode(expression.asMemberLookupExpression().memberExpression);
			qualifiedName.setName(getQualifiedName(
					expression.asMemberLookupExpression().memberExpression,
					qualifiedName).getName());
			return qualifiedName.setParent(getQualifiedName(
					expression.asMemberLookupExpression().operand,
					qualifiedName.createParent()));
		} else if (expression instanceof ParenExpressionTree) {
			qualifiedName.setNode(expression.asParenExpression());
			return qualifiedName.setParent(getQualifiedName(
					expression.asParenExpression().expression,
					qualifiedName.createParent()));
		} else if (expression instanceof CallExpressionTree) {
			qualifiedName.setNode(expression.asCallExpression());
			return qualifiedName.setParent(getQualifiedName(
					expression.asCallExpression().operand,
					qualifiedName.createParent()));
		} else {
			log.warn(expression.getClass());
			return null;
		}
	}
	
	public static String normalizeQualifiedNames(QualifiedName qualifiedName){
		return "";
	}
}
