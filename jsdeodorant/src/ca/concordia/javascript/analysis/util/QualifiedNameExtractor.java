package ca.concordia.javascript.analysis.util;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class QualifiedNameExtractor {
	private static final Logger log = Logger
			.getLogger(QualifiedNameExtractor.class.getName());

	public static String getQualifiedName(ParseTree expression) {
		if (expression instanceof IdentifierExpressionTree)
			return expression.asIdentifierExpression().identifierToken.value;
		else if (expression instanceof MemberExpressionTree)
			return expression.asMemberExpression().memberName.value;
		else if (expression instanceof MemberLookupExpressionTree)
			return getQualifiedName(expression.asMemberLookupExpression().operand);
		else if (expression instanceof ParenExpressionTree)
			return getQualifiedName(expression.asParenExpression().expression);
		else if (expression instanceof CallExpressionTree)
			return getQualifiedName(expression.asCallExpression().operand);
		else if (expression instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclaration = expression
					.asFunctionDeclaration();
			if (functionDeclaration.name != null)
				log.warn("Named function declaration as the right operand of new expression, is it correct?");
			return "AnonymousFunction at:"
					+ expression.asFunctionDeclaration().location;
		} else {
			log.warn(expression.getClass());
			return "";
		}
	}
}
