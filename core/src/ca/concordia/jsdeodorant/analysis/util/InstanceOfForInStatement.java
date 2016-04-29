package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ForInStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfForInStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof ForInStatementTree)
			return true;
		else
			return false;
	}
}
