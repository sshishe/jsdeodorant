package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public abstract class AbstractStatement extends AbstractFunctionFragment
		implements SourceElement {
	private ParseTree statement;
	private StatementType type;

	public AbstractStatement(ParseTree statement, StatementType type,
			SourceContainer parent) {
		super(parent);
		this.type = type;
		this.statement = statement;
	}

	public ParseTree getStatement() {
		return statement;
	}

	protected StatementType getType() {
		return type;
	}
}
