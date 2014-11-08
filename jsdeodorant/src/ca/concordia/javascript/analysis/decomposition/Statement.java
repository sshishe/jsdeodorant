package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import ca.concordia.javascript.analysis.util.ExpressionExtractor;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Statement extends AbstractStatement {

	public Statement(ParseTree statement, StatementType type,
			CompositeStatement parent) {
		super(statement, type, parent);
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		List<ParseTree> variableDeclarations = expressionExtractor
				.getVariableDeclarationExpressions(statement);
	}

	public String toString() {
		return getStatement().toString();
	}

}
