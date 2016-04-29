package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfOrOperator implements LogicalExpressionInstanceChecker {
	public boolean instanceOf(ParseTree expression) {
		if (expression instanceof BinaryOperatorTree)
			if (expression.asBinaryOperator().operator.toString().equals("||"))
				return true;
		return false;
	}
}
