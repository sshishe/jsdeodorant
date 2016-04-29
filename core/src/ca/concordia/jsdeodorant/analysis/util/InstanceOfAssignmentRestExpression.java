package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.AssignmentRestElementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfAssignmentRestExpression implements
		ExpressionInstanceChecker {
	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof AssignmentRestElementTree;
	}
}
