package ca.concordia.javascript.analysis.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationStatement;
import ca.concordia.javascript.analysis.decomposition.ObjectLiteralExpression;

public class ClassHelper {
	static Logger log = Logger.getLogger(ClassHelper.class.getName());

	public static List<FunctionDeclaration> getInnerFunctionList(SourceContainer container) {
		if (container instanceof Program) {
			Program program = (Program) container;
			return program.getFunctionDeclarationList();
		} else if (container instanceof CompositeStatement) {
			CompositeStatement composite = (CompositeStatement) container;
			return composite.getFunctionDeclarationList();
		}
		return null;
	}

	public static List<ObjectLiteralExpression> getObjectLiteralList(SourceContainer container) {
		if (container instanceof Program) {
			Program program = (Program) container;
			return program.getObjectLiteralList();
		} else if (container instanceof CompositeStatement) {
			CompositeStatement composite = (CompositeStatement) container;
			return composite.getObjectLiteralExpressionList();
		}
		return null;
	}

	public static List<FunctionDeclaration> getInnerFunctionList(FunctionDeclaration parentFunction) {
		if (parentFunction instanceof FunctionDeclarationStatement)
			return ((FunctionDeclarationStatement) parentFunction).getFunctionDeclarationList();
		else if (parentFunction instanceof FunctionDeclarationExpression)
			return ((FunctionDeclarationExpression) parentFunction).getFunctionDeclarationList();
		return null;
	}

	public static AbstractIdentifier checkEqualsBasedOnNamespace(FunctionDeclaration functionDeclaration) {
		if (functionDeclaration instanceof FunctionDeclarationStatement) {
			FunctionDeclarationStatement function = (FunctionDeclarationStatement) functionDeclaration;
			return function.getIdentifier();
		} else if (functionDeclaration instanceof FunctionDeclarationExpression) {
			FunctionDeclarationExpression function = (FunctionDeclarationExpression) functionDeclaration;
			SourceContainer parentElement = function.getParent();
			if (parentElement instanceof ObjectLiteralExpression) {
				ObjectLiteralExpression objectLiteralExpression = (ObjectLiteralExpression) parentElement;
				AbstractIdentifier parentIdentifier = objectLiteralExpression.getIdentifier();
				if (objectLiteralExpression.getParentFunction() != null) {
					List<AbstractStatement> statements = ((FunctionDeclaration) objectLiteralExpression.getParentFunction()).getStatements();
					for (AbstractStatement abstractStatement : statements) {
						ExpressionExtractor expressionExtractor = new ExpressionExtractor();
						List<ParseTree> binaryOperations = expressionExtractor.getBinaryOperators(abstractStatement.getStatement());
						for (ParseTree binaryOperationTree : binaryOperations) {
							BinaryOperatorTree binaryOperation = binaryOperationTree.asBinaryOperator();
							if (IdentifierHelper.getIdentifier(binaryOperation.right).equals(objectLiteralExpression.getIdentifier())) {
								parentIdentifier = IdentifierHelper.getIdentifier(binaryOperation.left);
							}
						}
					}
				}
				AbstractIdentifier identifier = function.getIdentifier();
				if (parentIdentifier instanceof PlainIdentifier)
					return new CompositeIdentifier(parentIdentifier.getNode(), identifier);
				else
					return new CompositeIdentifier(parentIdentifier.asCompositeIdentifier().getRightPart().getNode(), identifier);
			}
			if (parentElement instanceof CompositeStatement && ((CompositeStatement) parentElement).getParent() instanceof FunctionDeclarationExpression)
				parentElement = (FunctionDeclarationExpression) ((CompositeStatement) parentElement).getParent();
			if (parentElement instanceof FunctionDeclarationExpression) {
				FunctionDeclarationExpression parentFunctionDeclarationExpression = (FunctionDeclarationExpression) parentElement;
				AbstractIdentifier identifier = function.getIdentifier();
				switch (parentFunctionDeclarationExpression.getFunctionDeclarationExpressionNature()) {
				case NEW_FUNCTION:
					if (IdentifierHelper.isCompositeIdentifier(identifier)) {
						if (identifier.asCompositeIdentifier().getMostLeftPart().equals("this"))
							return identifier.asCompositeIdentifier().getRightPart();
					}
					break;
				case IIFE:
					List<AbstractStatement> returnStatements = parentFunctionDeclarationExpression.getReturnStatementList();
					if (returnStatements != null && !returnStatements.isEmpty()) {
						if (IdentifierHelper.isCompositeIdentifier(identifier)) {
							for (AbstractStatement statement : returnStatements) {
								if (IdentifierHelper.getIdentifier(statement.getStatement().asReturnStatement().expression).equals(identifier.asCompositeIdentifier().getMostLeftPart()))
									return identifier.asCompositeIdentifier().getRightPart();
							}
						}
					}
					break;
				default:
					return null;
				}
			}
		}
		return null;
	}
}
