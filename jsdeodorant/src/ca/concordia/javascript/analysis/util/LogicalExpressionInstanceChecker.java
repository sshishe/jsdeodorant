package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public interface LogicalExpressionInstanceChecker {
	public boolean instanceOf(ParseTree expression);
}
