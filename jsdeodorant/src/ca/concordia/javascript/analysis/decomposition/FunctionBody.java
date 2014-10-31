package ca.concordia.javascript.analysis.decomposition;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.BreakStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.CaseClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.CatchTree;
import com.google.javascript.jscomp.parsing.parser.trees.ContinueStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DebuggerStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.EmptyStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FinallyTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForInStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.SwitchStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TryStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;

public class FunctionBody {

	/**
	 * 
	 */
	private CompositeStatement bodyBlock;

	/**
	 * 
	 */
	private AbstractExpression bodyExpression;

	public FunctionBody(BlockTree blockTree) {
		this.bodyBlock = new CompositeStatement(blockTree, StatementType.BLOCK,
				null);
		ImmutableList<ParseTree> statements = blockTree.statements;
		for (ParseTree statement : statements) {
			processStatement(statement, bodyBlock);
		}
	}

	public FunctionBody(ParseTree expression) {

	}

	private void processStatement(ParseTree statement, CompositeStatement parent) {
		if (statement instanceof BlockTree) {
			BlockTree block = statement.asBlock();
			CompositeStatement child = new CompositeStatement(block,
					StatementType.BLOCK, parent);
			parent.addStatement(child);
			ImmutableList<ParseTree> statements = block.statements;
			for (ParseTree blockStatement : statements) {
				processStatement(blockStatement, child);
			}
		}

		else if (statement instanceof IfStatementTree) {
			IfStatementTree ifStatement = statement.asIfStatement();
			CompositeStatement child = new CompositeStatement(ifStatement,
					StatementType.IF, parent);
			AbstractExpression conditionExpression = new AbstractExpression(
					ifStatement.condition, child);
			child.addExpression(conditionExpression);
			parent.addStatement(child);
			processStatement(ifStatement.ifClause, child);
			if (ifStatement.elseClause != null)
				processStatement(ifStatement.elseClause, child);
		}

		else if (statement instanceof WhileStatementTree) {
			WhileStatementTree whileStatement = statement.asWhileStatement();
			CompositeStatement child = new CompositeStatement(whileStatement,
					StatementType.WHILE, parent);
			AbstractExpression conditionExpression = new AbstractExpression(
					whileStatement.condition, child);
			child.addExpression(conditionExpression);
			parent.addStatement(child);
			processStatement(whileStatement.body, child);
		}

		else if (statement instanceof DoWhileStatementTree) {
			DoWhileStatementTree doWhileStatement = statement
					.asDoWhileStatement();
			CompositeStatement child = new CompositeStatement(doWhileStatement,
					StatementType.DO_WHILE, parent);
			AbstractExpression conditionExpression = new AbstractExpression(
					doWhileStatement.condition, child);
			child.addExpression(conditionExpression);
			parent.addStatement(child);
			processStatement(doWhileStatement.body, child);
		}

		else if (statement instanceof ForInStatementTree) {
			ForInStatementTree forInStatement = statement.asForInStatement();
			CompositeStatement child = new CompositeStatement(forInStatement,
					StatementType.FOR_IN, parent);

			AbstractExpression initializerExpression = new AbstractExpression(
					forInStatement.initializer, child);
			child.addExpression(initializerExpression);

			AbstractExpression collectionExpression = new AbstractExpression(
					forInStatement.collection, child);
			child.addExpression(collectionExpression);

			parent.addStatement(child);
			processStatement(forInStatement.body, child);
		}

		else if (statement instanceof ForStatementTree) {
			ForStatementTree forStatement = statement.asForStatement();
			CompositeStatement child = new CompositeStatement(forStatement,
					StatementType.FOR, parent);

			if (forStatement.initializer != null) {
				AbstractExpression initializerExpression = new AbstractExpression(
						forStatement.initializer, child);
				child.addExpression(initializerExpression);
			}
			if (forStatement.condition != null) {
				AbstractExpression conditionExpression = new AbstractExpression(
						forStatement.condition, child);
				child.addExpression(conditionExpression);
			}
			if (forStatement.increment != null) {
				AbstractExpression incrementExpression = new AbstractExpression(
						forStatement.increment, child);
				child.addExpression(incrementExpression);
			}
			parent.addStatement(child);
			processStatement(forStatement.body, child);
		}

		else if (statement instanceof ForOfStatementTree) {
			ForOfStatementTree forOfStatement = statement.asForOfStatement();
			CompositeStatement child = new CompositeStatement(forOfStatement,
					StatementType.FOR_OF, parent);

			AbstractExpression initializerExpression = new AbstractExpression(
					forOfStatement.initializer, child);
			child.addExpression(initializerExpression);

			AbstractExpression collectionExpression = new AbstractExpression(
					forOfStatement.collection, child);
			child.addExpression(collectionExpression);

			parent.addStatement(child);
			processStatement(forOfStatement.body, child);
		}

		else if (statement instanceof WithStatementTree) {
			WithStatementTree withStatement = statement.asWithStatement();
			CompositeStatement child = new CompositeStatement(withStatement,
					StatementType.WITH, parent);

			AbstractExpression expression = new AbstractExpression(
					withStatement.expression, child);
			child.addExpression(expression);

			parent.addStatement(child);
			processStatement(withStatement.body, child);
		}

		else if (statement instanceof SwitchStatementTree) {
			SwitchStatementTree switchStatement = statement.asSwitchStatement();
			CompositeStatement child = new CompositeStatement(switchStatement,
					StatementType.SWITCH, parent);

			AbstractExpression expression = new AbstractExpression(
					switchStatement.expression, child);
			child.addExpression(expression);

			parent.addStatement(child);

			for (ParseTree caseClause : switchStatement.caseClauses) {
				processStatement(caseClause, child);
			}
		}

		else if (statement instanceof CaseClauseTree) {
			CaseClauseTree caseClause = statement.asCaseClause();
			CompositeStatement child = new CompositeStatement(caseClause,
					StatementType.CASE_CLAUSE, parent);

			AbstractExpression expression = new AbstractExpression(
					caseClause.expression, child);
			child.addExpression(expression);

			parent.addStatement(child);

			for (ParseTree caseClauseStatement : caseClause.statements) {
				processStatement(caseClauseStatement, child);
			}
		}

		else if (statement instanceof DefaultClauseTree) {
			DefaultClauseTree defaultClause = statement.asDefaultClause();
			CompositeStatement child = new CompositeStatement(defaultClause,
					StatementType.DEFAULT_CLAUSE, parent);

			parent.addStatement(child);

			for (ParseTree defaultClauseStatement : defaultClause.statements) {
				processStatement(defaultClauseStatement, child);
			}
		}

		else if (statement instanceof TryStatementTree) {
			TryStatementTree tryStatement = statement.asTryStatement();
			TryStatement child = new TryStatement(tryStatement, parent);
			if (tryStatement.catchBlock != null) {
				processStatement(tryStatement.catchBlock, child);
			}
			parent.addStatement(child);

			processStatement(tryStatement.body, child);

			if (tryStatement.catchBlock != null) {
				CatchTree catchBlock = tryStatement.catchBlock.asCatch();

				CompositeStatement catchClause = new CompositeStatement(
						catchBlock, StatementType.CATCH, null);

				AbstractExpression expression = new AbstractExpression(
						catchBlock.exception, catchClause);
				catchClause.addExpression(expression);

				processStatement(catchBlock.catchBody, catchClause);

				child.setCatchClause(catchClause);
			}

			if (tryStatement.finallyBlock != null) {
				FinallyTree finallyBlock = tryStatement.finallyBlock
						.asFinally();

				CompositeStatement finallyClause = new CompositeStatement(
						finallyBlock, StatementType.BLOCK, null);

				processStatement(finallyBlock.block, finallyClause);

				child.setFinally(finallyClause);
			}
		}

		else if (statement instanceof LabelledStatementTree) {
			LabelledStatementTree labelledStatement = statement
					.asLabelledStatement();

			LabelledStatement child = new LabelledStatement(labelledStatement,
					parent);

			child.setLabel(labelledStatement.name.value);

			parent.addStatement(child);

			processStatement(labelledStatement.statement, child);
		}

		else if (statement instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = statement
					.asVariableStatement();

			Statement child = new Statement(variableStatement,
					StatementType.VARIABLE, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof EmptyStatementTree) {
			EmptyStatementTree emptyStatement = statement.asEmptyStatement();

			Statement child = new Statement(emptyStatement,
					StatementType.EMPTY, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = statement
					.asExpressionStatement();

			Statement child = new Statement(expressionStatement,
					StatementType.EXPRESSION, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof BreakStatementTree) {
			BreakStatementTree breakStatement = statement.asBreakStatement();

			Statement child = new Statement(breakStatement,
					StatementType.BREAK, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof ContinueStatementTree) {
			ContinueStatementTree continueStatement = statement
					.asContinueStatement();

			Statement child = new Statement(continueStatement,
					StatementType.CONTINUE, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof ReturnStatementTree) {
			ReturnStatementTree returnStatement = statement.asReturnStatement();

			Statement child = new Statement(returnStatement,
					StatementType.RETURN, parent);

			parent.addStatement(child);
		}

		else if (statement instanceof ThrowStatementTree) {
			ThrowStatementTree thowStatement = statement.asThrowStatement();

			Statement child = new Statement(thowStatement, StatementType.THROW,
					parent);

			parent.addStatement(child);
		}

		else if (statement instanceof DebuggerStatementTree) {
			DebuggerStatementTree debuggerStatement = statement
					.asDebuggerStatement();

			Statement child = new Statement(debuggerStatement,
					StatementType.DEBUGGER, parent);

			parent.addStatement(child);
		}

	}
}
