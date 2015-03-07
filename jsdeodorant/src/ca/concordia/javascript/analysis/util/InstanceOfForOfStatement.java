package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ForOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfForOfStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof ForOfStatementTree)
			return true;
		else
			return false;
	}
}
