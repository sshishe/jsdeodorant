package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfParseTree implements ExpressionInstanceChecker {
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof ParseTree;
	}
}
