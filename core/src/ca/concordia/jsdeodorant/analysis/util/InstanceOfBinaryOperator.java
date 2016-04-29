package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfBinaryOperator implements ExpressionInstanceChecker {
	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof BinaryOperatorTree;
	}
}
