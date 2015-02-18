package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

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

		processNewExpressions(expressionExtractor.getNewExpressions(statement));

		processObjectLiteralExpressions(expressionExtractor
				.getObjectLiteralExpressions(statement));

		processArrayLiteralExpressions(expressionExtractor
				.getArrayLiteralExpressions(statement));
	}
	
	@Override
	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		List<FunctionDeclarationExpression> functionDeclarationExpressions =
				this.getFuntionDeclarationExpressions();
		functionDeclarations.addAll(functionDeclarationExpressions);
		for (FunctionDeclarationExpression expression : functionDeclarationExpressions) {
			functionDeclarations.addAll(expression.getFunctionDeclarations());
		}
		return functionDeclarations;
	}

	public String toString() {
		return SourceHelper.extract(getStatement());
	}
}
