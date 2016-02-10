package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;
import ca.concordia.javascript.analysis.util.DebugHelper;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Statement extends AbstractStatement {
	public Statement(ParseTree statement, StatementType type, SourceContainer parent) {
		super(statement, type, parent);
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		processFunctionInvocations(expressionExtractor.getCallExpressions(statement));
		processVariableDeclarations(expressionExtractor.getVariableDeclarationExpressions(statement));
		processNewExpressions(expressionExtractor.getNewExpressions(statement));
		processArrayLiteralExpressions(expressionExtractor.getArrayLiteralExpressions(statement));
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
//		List<FunctionInvocation> functionInvocations = this.getFunctionInvocationList();
//		for (FunctionInvocation functionInvocation : functionInvocations) {
//			List<AbstractExpression> arguments = functionInvocation.getArguments();
//			for (AbstractExpression argument : arguments) {
//				if (argument.getExpression() instanceof FunctionDeclarationTree) {
//					functionDeclarationList.add(new FunctionDeclarationExpression(argument.getExpression().asFunctionDeclaration(), FunctionDeclarationExpressionNature.PARAMETER, this.getParent()));
////					List<FunctionDeclarationExpression> functionDeclarationExpressionsArgument = argument.getFunctionDeclarationExpressionList();
////					functionDeclarationList.addAll(functionDeclarationExpressionsArgument);
////					for (FunctionDeclarationExpression expression : functionDeclarationExpressionsArgument) {
////						functionDeclarationList.addAll(expression.getFunctionDeclarationList());
////					}
//				}
//			}
//		}
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
