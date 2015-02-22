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

		processArrayLiteralExpressions(expressionExtractor
				.getArrayLiteralExpressions(statement));
	}

	@Override
	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		List<FunctionDeclarationExpression> functionDeclarationExpressions = this
				.getFuntionDeclarationExpressions();
		functionDeclarations.addAll(functionDeclarationExpressions);
		for (FunctionDeclarationExpression expression : functionDeclarationExpressions) {
			functionDeclarations.addAll(expression.getFunctionDeclarations());
		}
		List<ObjectLiteralExpression> objectLiteralExpressions = this
				.getObjectLiteralExpressionList();
		for (ObjectLiteralExpression objectLiteralExpression : objectLiteralExpressions) {
			functionDeclarations.addAll(objectLiteralExpression
					.getFunctionDeclarations());
		}
		return functionDeclarations;
	}

	@Override
	public List<ObjectLiteralExpression> getObjectLiterals() {
		List<ObjectLiteralExpression> objectLiterals = new ArrayList<>();
		for (ObjectLiteralExpression expression : this
				.getObjectLiteralExpressionList()) {
			objectLiterals.add(expression);
			objectLiterals.addAll(expression.getObjectLiterals());
		}
		return objectLiterals;
	}

	public String toString() {
		return SourceHelper.extract(getStatement());
	}
}
