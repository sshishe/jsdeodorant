package ca.concordia.javascript.metrics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationStatement;
import ca.concordia.javascript.analysis.util.LogicalExpressionExtractor;
import ca.concordia.javascript.analysis.util.StatementExtractor;

public class CyclomaticComplexity {
	static Logger log = Logger.getLogger(CyclomaticComplexity.class.getName());
	private Program program;
	private Map<String, Integer> functionsComplexity;
	private StatementExtractor statementExtractor;
	private LogicalExpressionExtractor logicalExpressionExtractor;

	public CyclomaticComplexity(Program program) {
		this.program = program;
		functionsComplexity = new LinkedHashMap<String, Integer>();
		statementExtractor = new StatementExtractor();
		logicalExpressionExtractor = new LogicalExpressionExtractor();
	}

	public Map<String, Integer> calculate() {
		for (FunctionDeclaration functionDeclaration : program
				.getFunctionDeclarations()) {
			int functionComplexity = 1;
			if (functionDeclaration instanceof FunctionDeclarationStatement) {
				FunctionDeclarationStatement functionDeclarationStatement = (FunctionDeclarationStatement) functionDeclaration;
				functionComplexity = processStatement(
						(CompositeStatement) functionDeclarationStatement
								.getStatements().get(0),
						functionDeclarationStatement);
			}
			if (functionDeclaration instanceof FunctionDeclarationExpression) {
				FunctionDeclarationExpression functionDeclarationExpression = (FunctionDeclarationExpression) functionDeclaration;
				functionComplexity = processStatement(
						(CompositeStatement) functionDeclarationExpression
								.getStatements().get(0),
						functionDeclarationExpression);
			}
			functionsComplexity.put(functionDeclaration.getName(),
					functionComplexity);
		}
		return functionsComplexity;
	}

	private int processStatement(CompositeStatement statements,
			FunctionDeclaration functionDeclaration) {
		int complexity = 1;
		List<ParseTree> checkedCompoundConditions = new ArrayList<ParseTree>();
		for (AbstractStatement statement : statements.getStatements()) {
			if (!isFunctionDeclaration(statement)) {
				List<ParseTree> conditionalStatements = statementExtractor
						.getConditionalStatements(statement.getStatement());

				for (ParseTree conditionalStatement : conditionalStatements) {
					complexity++;
					if (conditionalStatement instanceof IfStatementTree
							|| conditionalStatement instanceof ReturnStatementTree) {
						List<ParseTree> compoundConditions = logicalExpressionExtractor
								.getAndExpressions(conditionalStatement);

						compoundConditions.addAll(logicalExpressionExtractor
								.getOrExpressions(conditionalStatement));

						// Remove compound conditions (&& and ||) from if
						// conditions if they were checked before
						Iterator<ParseTree> i = compoundConditions.iterator();
						while (i.hasNext()) {
							ParseTree condition = i.next();
							if (checkedCompoundConditions.contains(condition))
								i.remove();
							else
								checkedCompoundConditions.add(condition);
						}
						// Add number of compound conditions exist in current
						// context
						if (compoundConditions != null
								&& !compoundConditions.isEmpty())
							complexity += compoundConditions.size();
					}
				}
			}
			// if last statement of function is return we have to reduce one
			// because we incremented it. it is not increasing complexity
			if (statements.getStatements()
					.get(statements.getStatements().size() - 1).getStatement() instanceof ReturnStatementTree)
				complexity--;
		}

		return complexity;
	}

	private boolean isFunctionDeclaration(AbstractStatement statement) {
		if (statement instanceof FunctionDeclaration)
			return true;
		if (statement.getStatement() instanceof ExpressionStatementTree)
			if (statement.getStatement().asExpressionStatement().expression instanceof BinaryOperatorTree)
				if (statement.getStatement().asExpressionStatement().expression
						.asBinaryOperator().right instanceof FunctionDeclarationTree)
					return true;
		if (statement.getStatement() instanceof VariableStatementTree)
			for (VariableDeclarationTree variableDeclaration : statement
					.getStatement().asVariableStatement().declarations.declarations)
				if (variableDeclaration.initializer instanceof FunctionDeclarationTree)
					return true;
		return false;
	}
}
