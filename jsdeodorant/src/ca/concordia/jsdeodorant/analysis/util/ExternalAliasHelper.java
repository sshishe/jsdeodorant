package ca.concordia.jsdeodorant.analysis.util;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceElement;
import ca.concordia.jsdeodorant.analysis.abstraction.VariableDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.CompositeStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.Statement;

public class ExternalAliasHelper {
	public static AbstractIdentifier getAliasedIdentifier(AbstractFunctionFragment container, AbstractIdentifier currentIdentifier) {
		AbstractIdentifier aliasedIdentifier = null;
		if (currentIdentifier instanceof PlainIdentifier)
			return currentIdentifier;
		if (container.getParent() instanceof CompositeStatement)
			aliasedIdentifier = inspectCompositeStatement((CompositeStatement) container.getParent(), currentIdentifier);
		else if (container.getParent() instanceof Program) {
			Program parent = ((Program) container.getParent());
			for (SourceElement sourceElement : parent.getSourceElements()) {
				if (sourceElement instanceof Statement) {
					Statement statement = (Statement) sourceElement;
					if (!container.equals(statement))
						if (statement.getStatement() instanceof VariableStatementTree || statement.getStatement() instanceof ExpressionStatementTree)
							aliasedIdentifier = detectAliasing(statement, (CompositeIdentifier) currentIdentifier);
						else
							aliasedIdentifier = detectAliasing(statement.getVariableDeclarationList(), currentIdentifier);
				} else if (sourceElement instanceof CompositeStatement)
					aliasedIdentifier = inspectCompositeStatement((CompositeStatement) sourceElement, currentIdentifier);
				if (aliasedIdentifier != null)
					return aliasedIdentifier;
			}
		}
		return currentIdentifier;
	}

	private static AbstractIdentifier inspectCompositeStatement(CompositeStatement composite, AbstractIdentifier currentIdentifier) {
		for (AbstractStatement statement : composite.getStatements()) {
			if (statement.getStatement() instanceof VariableStatementTree || statement.getStatement() instanceof ExpressionStatementTree) {
				AbstractIdentifier aliasedIdentifier = detectAliasing(statement, (CompositeIdentifier) currentIdentifier);
				if (aliasedIdentifier != null)
					return aliasedIdentifier;
			}
		}
		return null;
	}

	private static AbstractIdentifier detectAliasing(List<VariableDeclaration> variableDeclarations, AbstractIdentifier identifier) {
		for (VariableDeclaration variableDeclaration : variableDeclarations) {
			AbstractIdentifier aliasedIdentifier = detectAliasing(variableDeclaration, identifier);
			if (aliasedIdentifier != null)
				return aliasedIdentifier;
		}
		return null;
	}

	private static AbstractIdentifier detectAliasing(VariableDeclaration variableDeclaration, AbstractIdentifier identifier) {
		try {
			throw new Exception("This method should not be reached");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return identifier;
	}

	private static AbstractIdentifier detectAliasing(AbstractStatement statement, CompositeIdentifier identifier) {
		if (statement.getStatement() instanceof VariableStatementTree) {
			VariableStatementTree variableStatementTree = statement.getStatement().asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatementTree.declarations.declarations) {
				if (variableDeclaration.initializer == null)
					continue;
				// if new function() IIFE then continue, do not change the identifier
				if (variableDeclaration.initializer instanceof NewExpressionTree && variableDeclaration.initializer.asNewExpression().operand instanceof FunctionDeclarationTree)
					continue;
				if (IdentifierHelper.getIdentifier(variableDeclaration.lvalue).getIdentifierName().equals(identifier.getMostLeftPart().getIdentifierName()))
					if (variableDeclaration.initializer instanceof NewExpressionTree)
						return identifier.getRightPart();
					else
						return identifier;
			}
		} else if (statement.getStatement() instanceof ExpressionStatementTree) {
			ExpressionStatementTree expression = statement.getStatement().asExpressionStatement();
			if (expression.expression instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperator = expression.expression.asBinaryOperator();
				if (binaryOperator.right == null)
					return null;
				if (IdentifierHelper.getIdentifier(binaryOperator.left).getIdentifierName().equals(identifier.getMostLeftPart().getIdentifierName()))
					if (binaryOperator.right instanceof NewExpressionTree)
						return identifier.getRightPart();
					else
						return new CompositeIdentifier(IdentifierHelper.getIdentifier(binaryOperator.right), identifier.getRightPart());
			}
		}

		return null;
	}
}
