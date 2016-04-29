package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.CaseClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfCaseClause implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		if (statement instanceof CaseClauseTree)
			return true;
		else
			return false;
	}
}
