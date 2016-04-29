package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfConditionalStatements implements StatementInstanceChecker {
	public boolean instanceOf(ParseTree statement) {
		return (new InstanceOfIfStatement().instanceOf(statement)
				|| new InstanceOfWhileStatement().instanceOf(statement)
				|| new InstanceOfDoWhileStatement().instanceOf(statement)
				|| new InstanceOfForStatement().instanceOf(statement)
				|| new InstanceOfForInStatement().instanceOf(statement)
				|| new InstanceOfForOfStatement().instanceOf(statement) || new InstanceOfCaseClause()
.instanceOf(statement) || new InstanceOfCatchClause()
					.instanceOf(statement) ||
 new InstanceOfReturnStatement()
					.instanceOf(statement));

	}
}
