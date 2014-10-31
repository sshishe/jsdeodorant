package ca.concordia.javascript.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Statement extends AbstractStatement {

	public Statement(ParseTree statement, StatementType type,
			CompositeStatement parent) {
		super(statement, type, parent);
	}

	public String toString() {
		return getStatement().toString();
	}

}
