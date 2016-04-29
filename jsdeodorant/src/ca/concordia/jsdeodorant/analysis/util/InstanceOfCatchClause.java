package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.CatchTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfCatchClause implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof CatchTree)
			return true;
		else
			return false;
	}
}
