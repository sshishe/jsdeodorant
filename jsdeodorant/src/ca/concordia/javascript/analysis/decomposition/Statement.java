package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;
import ca.concordia.javascript.analysis.util.SourceHelper;

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

		processAnonymousFunctionDeclarations(expressionExtractor
				.getAnonymousFunctionExpressions(statement));

		processNewExpressions(expressionExtractor.getNewExpressions(statement));

		processObjectLiteralExpressions(expressionExtractor
				.getObjectLiteralExpressions(statement));

		processArrayLiteralExpressions(expressionExtractor
				.getArrayLiteralExpressions(statement));
	}

	public String toString() {
		return SourceHelper.extract(getStatement());
	}
}
