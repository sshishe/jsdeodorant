package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.CommaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfCommaExpression implements ExpressionInstanceChecker {
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof CommaExpressionTree;
	}
}
