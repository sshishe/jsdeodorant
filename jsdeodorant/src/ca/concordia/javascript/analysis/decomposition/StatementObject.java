package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class StatementObject extends AbstractStatement {

	public StatementObject(ParseTree statement, StatementType type,
			CompositeStatement parent) {
		super(statement, type, parent);
		ParseTree assignments = statement.asAssignmentRestElement();
	}

}
