package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ForStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfForStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof ForStatementTree)
			return true;
		else
			return false;
	}
}
