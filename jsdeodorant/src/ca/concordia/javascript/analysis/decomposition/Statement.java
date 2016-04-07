package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.DebugHelper;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;

public class Statement extends AbstractStatement {
	public Statement(ParseTree statement, StatementType type, SourceContainer parent) {
		super(statement, type, parent);
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		processFunctionInvocations(expressionExtractor.getCallExpressions(statement));
		processVariableDeclarations(expressionExtractor.getVariableDeclarationExpressions(statement));
		processNewExpressions(expressionExtractor.getNewExpressions(statement));
		processArrayLiteralExpressions(expressionExtractor.getArrayLiteralExpressions(statement));
		processAssignmentExpressions(expressionExtractor.getBinaryOperators(statement));
	}

	@Override
	public List<FunctionDeclaration> getFunctionDeclarationList() {
		List<FunctionDeclaration> functionDeclarationList = new ArrayList<>();
		List<FunctionDeclarationExpression> functionDeclarationExpressions = this.getFunctionDeclarationExpressionList();
		functionDeclarationList.addAll(functionDeclarationExpressions);
		for (FunctionDeclarationExpression expression : functionDeclarationExpressions) {
			functionDeclarationList.addAll(expression.getFunctionDeclarationList());
		}
		List<ObjectLiteralExpression> objectLiteralExpressions = this.getObjectLiteralExpressionList();
		for (ObjectLiteralExpression objectLiteralExpression : objectLiteralExpressions) {
			functionDeclarationList.addAll(objectLiteralExpression.getFunctionDeclarations());
		}

		return functionDeclarationList;
	}

	@Override
	public List<ObjectLiteralExpression> getObjectLiteralList() {
		List<ObjectLiteralExpression> objectLiterals = new ArrayList<>();
		for (ObjectLiteralExpression expression : this.getObjectLiteralExpressionList()) {
			objectLiterals.add(expression);
			objectLiterals.addAll(expression.getObjectLiterals());
		}
		return objectLiterals;
	}

	public String toString() {
		return DebugHelper.extract(getStatement());
	}
}
