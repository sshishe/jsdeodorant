package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;

public abstract class AbstractStatement extends AbstractFunctionFragment {
	private ASTInformation statement;
	private StatementType type;

	public AbstractStatement(/* Statement statement, */StatementType type,
			AbstractFunctionFragment parent) {
		super(parent);
		this.type = type;
		this.statement = null;//ASTInformationGenerator.generateASTInformation(statement);
	}
}
