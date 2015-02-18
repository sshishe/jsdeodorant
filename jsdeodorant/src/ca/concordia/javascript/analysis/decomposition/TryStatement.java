package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class TryStatement extends CompositeStatement {

	private CompositeStatement catchClause;
	private CompositeStatement finallyBlock;

	public TryStatement(ParseTree statement, SourceContainer parent) {
		super(statement, StatementType.TRY, parent);
	}

	public void setCatchClause(CompositeStatement catchClause) {
		this.catchClause = catchClause;
	}

	public void setFinallyBlock(CompositeStatement finallyBlock) {
		this.finallyBlock = finallyBlock;
	}

	public CompositeStatement getCatchClause() {
		return catchClause;
	}

	public CompositeStatement getFinallyBlock() {
		return finallyBlock;
	}

}
