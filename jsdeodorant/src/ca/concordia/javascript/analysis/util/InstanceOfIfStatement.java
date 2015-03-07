package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfIfStatement implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof IfStatementTree)
			return true;
		else
			return false;
	}
}
