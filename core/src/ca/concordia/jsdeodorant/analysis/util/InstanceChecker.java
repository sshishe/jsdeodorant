package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public interface InstanceChecker {
	public boolean instanceOf(ParseTree statement);
}
