package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class CompositeStatement extends AbstractStatement {

	private List<AbstractStatement> statementList;
	private List<AbstractExpression> expressionList;

	public CompositeStatement(ParseTree statement, StatementType type,
			CompositeStatement parent) {
		super(statement, type, parent);
		statementList = new ArrayList<>();
		expressionList = new ArrayList<>();
	}

	public void addStatement(AbstractStatement statement) {
		statementList.add(statement);
	}

	public void addExpression(AbstractExpression expression) {
		expressionList.add(expression);
	}

	public List<AbstractStatement> getStatements() {
		return statementList;
	}

	public List<AbstractExpression> getExpressions() {
		return expressionList;
	}

}
