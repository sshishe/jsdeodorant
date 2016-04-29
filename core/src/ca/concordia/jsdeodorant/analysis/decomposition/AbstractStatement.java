package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceElement;
import ca.concordia.jsdeodorant.analysis.util.DebugHelper;

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

	public abstract List<FunctionDeclaration> getFunctionDeclarationList();
	public abstract List<ObjectLiteralExpression> getObjectLiteralList();

	public String toString() {
		return DebugHelper.extract(statement);
	}
}
