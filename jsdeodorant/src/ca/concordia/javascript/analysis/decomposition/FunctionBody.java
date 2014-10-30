package ca.concordia.javascript.analysis.decomposition;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;

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
			BlockTree block = (BlockTree) statement;
			CompositeStatement child = new CompositeStatement(block,
					StatementType.BLOCK, parent);
			parent.addStatement(child);
			ImmutableList<ParseTree> statements = block.statements;
			for (ParseTree blockStatement : statements) {
				processStatement(blockStatement, child);
			}
		}

		else if (statement instanceof IfStatementTree) {
			IfStatementTree ifStatement = (IfStatementTree) statement;
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
			WhileStatementTree whileStatement = (WhileStatementTree) statement;
			CompositeStatement child = new CompositeStatement(whileStatement,
					StatementType.WHILE, parent);
			AbstractExpression conditionExpression = new AbstractExpression(
					whileStatement.condition, child);
			child.addExpression(conditionExpression);
			parent.addStatement(child);
			processStatement(whileStatement.body, child);
		}

		else if (statement instanceof DoWhileStatementTree) {
			DoWhileStatementTree doWhileStatement = (DoWhileStatementTree) statement;
			CompositeStatement child = new CompositeStatement(doWhileStatement,
					StatementType.DO_WHILE, parent);
			AbstractExpression conditionExpression = new AbstractExpression(
					doWhileStatement.condition, child);
			child.addExpression(conditionExpression);
			parent.addStatement(child);
			processStatement(doWhileStatement, child);
		}

	}
}
