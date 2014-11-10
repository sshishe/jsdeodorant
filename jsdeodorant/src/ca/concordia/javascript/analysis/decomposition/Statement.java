package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Statement extends AbstractStatement {

	public Statement(ParseTree statement, StatementType type,
			SourceContainer parent) {
		super(statement, type, parent);
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();

		List<ParseTree> functionInvocations = expressionExtractor
				.getCallExpressions(statement);

		processFunctionInvocations(functionInvocations);

		List<ParseTree> functionDeclarations = expressionExtractor
				.getFunctionDeclarations(statement);

		processFunctionDeclarations(functionDeclarations);
	}

	public String toString() {
		return getStatement().toString();
	}

}
