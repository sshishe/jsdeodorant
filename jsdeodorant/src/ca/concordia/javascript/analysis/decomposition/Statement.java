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

		processFunctionInvocations(expressionExtractor
				.getCallExpressions(statement));

		processFunctionDeclarations(expressionExtractor
				.getFunctionDeclarations(statement));

		// used by arrayCreations and objectCreations
		List<ParseTree> newExpressions = expressionExtractor
				.getNewExpressions(statement);

		List<ParseTree> objectCreations = expressionExtractor
				.getObjectLiteralExpressions(statement);
		objectCreations.addAll(newExpressions);
		processObjectCreations(objectCreations);

		List<ParseTree> arrayCreations = expressionExtractor
				.getArrayLiteralExpressions(statement);
		objectCreations.addAll(newExpressions);
		processArrayCreations(arrayCreations);
	}

	public String toString() {
		return getStatement().toString();
	}
}
