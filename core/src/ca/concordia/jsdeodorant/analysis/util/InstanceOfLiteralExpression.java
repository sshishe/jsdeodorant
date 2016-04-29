package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfLiteralExpression implements ExpressionInstanceChecker {

	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof LiteralExpressionTree;
	}

}
