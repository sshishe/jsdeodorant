package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class CompositeStatement extends AbstractStatement implements
		SourceContainer {

	private List<AbstractStatement> statementList;
	private List<AbstractExpression> expressionList;

	public CompositeStatement(ParseTree statement, StatementType type,
			SourceContainer parent) {
		super(statement, type, parent);
		statementList = new ArrayList<>();
		expressionList = new ArrayList<>();
	}

	@Override
	public void addElement(SourceElement element) {
		if (element instanceof AbstractStatement)
			addStatement((AbstractStatement) element);
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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getType().toString());
		if (expressionList.size() > 0) {
			sb.append("(");
			for (int i = 0; i < expressionList.size() - 1; i++) {
				sb.append(expressionList.get(i).toString()).append("; ");
			}
			sb.append(expressionList.get(expressionList.size() - 1).toString());
			sb.append(")");
		}
		sb.append("\n");
		return sb.toString();
	}

}
