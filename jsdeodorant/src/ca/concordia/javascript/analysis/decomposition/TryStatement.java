package ca.concordia.javascript.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class TryStatement extends CompositeStatement {

	private CompositeStatement catchClause;
	private CompositeStatement finallyBlock;

	public TryStatement(ParseTree statement, CompositeStatement parent) {
		super(statement, StatementType.TRY, parent);

	}

	public void setCatchClause(CompositeStatement catchClause) {
		this.catchClause = catchClause;
	}

	public void setFinally(CompositeStatement finallyBlock) {
		this.finallyBlock = finallyBlock;
	}

}
