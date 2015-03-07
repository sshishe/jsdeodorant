package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;

public class InstanceOfWhileStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof WhileStatementTree)
			return true;
		else
			return false;
	}
}
