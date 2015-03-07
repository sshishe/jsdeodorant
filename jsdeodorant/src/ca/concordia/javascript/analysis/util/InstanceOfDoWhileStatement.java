package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfDoWhileStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof DoWhileStatementTree)
			return true;
		else
			return false;
	}
}
