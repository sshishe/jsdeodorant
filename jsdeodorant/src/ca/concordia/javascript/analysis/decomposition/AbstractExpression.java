package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;
import ca.concordia.javascript.analysis.util.SourceHelper;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class AbstractExpression extends AbstractFunctionFragment {

	private ParseTree expression;

	public AbstractExpression(ParseTree expression) {
		super(null);
		this.expression = expression;
	}

	public AbstractExpression(ParseTree expression, SourceContainer parent) {
		super(parent);
		this.expression = expression;
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();

		processFunctionInvocations(expressionExtractor
				.getCallExpressions(expression));

		processFunctionDeclarations(expressionExtractor
				.getFunctionDeclarations(expression));

		processAnonymousFunctionDeclarations(expressionExtractor
				.getAnonymousFunctionExpressions(expression));

		processNewExpressions(expressionExtractor.getNewExpressions(expression));

		processObjectLiteralExpressions(expressionExtractor
				.getObjectLiteralExpressions(expression));

		processArrayLiteralExpressions(expressionExtractor
				.getArrayLiteralExpressions(expression));
	}

	public ParseTree getExpression() {
		return expression;
	}
	
	public String toString() {
		return SourceHelper.extract(expression);
	}

}
