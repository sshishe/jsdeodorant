package ca.concordia.jsdeodorant.analysis.util;

import java.util.ArrayList;
import java.util.List;

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
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.SwitchStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TryStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;

public class StatementExtractor {
	private StatementInstanceChecker instanceChecker;

	public List<ParseTree> getIfStatements(ParseTree statement) {
		instanceChecker = new InstanceOfIfStatement();
		return getStatements(statement);
	}

	public List<ParseTree> getConditionalStatements(ParseTree statement) {
		instanceChecker = new InstanceOfConditionalStatements();
		return getStatements(statement);
	}

	public List<ParseTree> getReturnStatement(ParseTree statement) {
		instanceChecker = new InstanceOfReturnStatement();
		return getStatements(statement);
	}

	private List<ParseTree> getStatements(ParseTree element) {
		List<ParseTree> statementList = new ArrayList<ParseTree>();
		if (element instanceof ProgramTree) {
			ProgramTree program = element.asProgram();

			for (ParseTree sourceElement : program.sourceElements)
				statementList.addAll(getStatements(sourceElement));
		}

		else if (element instanceof BlockTree) {
			BlockTree block = element.asBlock();
			ImmutableList<ParseTree> statements = block.statements;
			for (ParseTree blockStatement : statements) {
				statementList.addAll(getStatements(blockStatement));
			}
		}

		else if (element instanceof IfStatementTree) {
			IfStatementTree ifStatement = element.asIfStatement();
			statementList.addAll(getStatements(ifStatement.ifClause));
			if (ifStatement.elseClause != null)
				statementList.addAll(getStatements(ifStatement.elseClause));

			if (instanceChecker.instanceOf(ifStatement))
				statementList.add(ifStatement);
		}

		else if (element instanceof WhileStatementTree) {
			WhileStatementTree whileStatement = element.asWhileStatement();
			statementList.addAll(getStatements(whileStatement.body));

			if (instanceChecker.instanceOf(whileStatement))
				statementList.add(whileStatement);
		}

		else if (element instanceof DoWhileStatementTree) {
			DoWhileStatementTree doWhileStatement = element
					.asDoWhileStatement();
			statementList.addAll(getStatements(doWhileStatement.body));

			if (instanceChecker.instanceOf(doWhileStatement))
				statementList.add(doWhileStatement);
		}

		else if (element instanceof ForInStatementTree) {
			ForInStatementTree forInStatement = element.asForInStatement();
			statementList.addAll(getStatements(forInStatement.body));

			if (instanceChecker.instanceOf(forInStatement))
				statementList.add(forInStatement);
		}

		else if (element instanceof ForStatementTree) {
			ForStatementTree forStatement = element.asForStatement();
			statementList.addAll(getStatements(forStatement.body));

			if (instanceChecker.instanceOf(forStatement))
				statementList.add(forStatement);
		}

		else if (element instanceof ForOfStatementTree) {
			ForOfStatementTree forOfStatement = element.asForOfStatement();
			statementList.addAll(getStatements(forOfStatement.body));

			if (instanceChecker.instanceOf(forOfStatement))
				statementList.add(forOfStatement);
		}

		else if (element instanceof WithStatementTree) {
			WithStatementTree withStatement = element.asWithStatement();
			statementList.addAll(getStatements(withStatement.body));

			if (instanceChecker.instanceOf(withStatement))
				statementList.add(withStatement);
		}

		else if (element instanceof SwitchStatementTree) {
			SwitchStatementTree switchStatement = element.asSwitchStatement();
			for (ParseTree caseClause : switchStatement.caseClauses)
				statementList.addAll(getStatements(caseClause));

			if (instanceChecker.instanceOf(switchStatement))
				statementList.add(switchStatement);
		}

		else if (element instanceof CaseClauseTree) {
			CaseClauseTree caseClause = element.asCaseClause();
			for (ParseTree caseClauseStatement : caseClause.statements) {
				statementList.addAll(getStatements(caseClauseStatement));
			}
		}

		else if (element instanceof DefaultClauseTree) {
			DefaultClauseTree defaultClause = element.asDefaultClause();
			for (ParseTree defaultClauseStatement : defaultClause.statements) {
				statementList.addAll(getStatements(defaultClauseStatement));
			}
		}

		else if (element instanceof TryStatementTree) {
			TryStatementTree tryStatement = element.asTryStatement();
			statementList.addAll(getStatements(tryStatement.body));

			if (tryStatement.catchBlock != null) {
				CatchTree catchBlock = tryStatement.catchBlock.asCatch();
				statementList.addAll(getStatements(catchBlock.catchBody));
				if (instanceChecker.instanceOf(catchBlock))
					statementList.add(catchBlock);
			}

			if (tryStatement.finallyBlock != null) {
				FinallyTree finallyBlock = tryStatement.finallyBlock
						.asFinally();
				statementList.addAll(getStatements(finallyBlock.block));
			}
		}

		else if (element instanceof LabelledStatementTree) {
			LabelledStatementTree labelledStatement = element
					.asLabelledStatement();
			statementList.addAll(getStatements(labelledStatement.statement));
		}

		else if (element instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = element
					.asVariableStatement();
			statementList.addAll(getStatements(variableStatement.declarations));
		}

		else if (element instanceof VariableDeclarationListTree) {
			VariableDeclarationListTree variableDeclarationList = element
					.asVariableDeclarationList();
			for (VariableDeclarationTree variableDeclaration : variableDeclarationList.declarations) {
				statementList.addAll(getStatements(variableDeclaration));
			}
		}

		else if (element instanceof VariableDeclarationTree) {
			VariableDeclarationTree variableDeclaration = element
					.asVariableDeclaration();
			statementList.addAll(getStatements(variableDeclaration.lvalue));
			statementList
					.addAll(getStatements(variableDeclaration.initializer));

			if (instanceChecker.instanceOf(variableDeclaration))
				statementList.add(variableDeclaration);
		}

		else if (element instanceof EmptyStatementTree) {
			EmptyStatementTree emptyStatement = element.asEmptyStatement();
		}

		else if (element instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = element
					.asExpressionStatement();
		}

		else if (element instanceof BreakStatementTree) {
			BreakStatementTree breakStatement = element.asBreakStatement();
			if (instanceChecker.instanceOf(breakStatement))
				statementList.add(breakStatement);
		}

		else if (element instanceof ContinueStatementTree) {
			ContinueStatementTree continueStatement = element
					.asContinueStatement();
			if (instanceChecker.instanceOf(continueStatement))
				statementList.add(continueStatement);
		}

		else if (element instanceof ReturnStatementTree) {
			ReturnStatementTree returnStatement = element.asReturnStatement();
			if (instanceChecker.instanceOf(returnStatement))
				statementList.add(returnStatement);
		}

		else if (element instanceof ThrowStatementTree) {
			ThrowStatementTree throwStatement = element.asThrowStatement();
			if (instanceChecker.instanceOf(throwStatement))
				statementList.add(throwStatement);
		}

		else if (element instanceof DebuggerStatementTree) {
			DebuggerStatementTree debuggerStatement = element
					.asDebuggerStatement();
		}

		else if (element instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclarationStatement = element
					.asFunctionDeclaration();
			statementList
					.addAll(getStatements(functionDeclarationStatement.functionBody));
			if (instanceChecker.instanceOf(functionDeclarationStatement))
				statementList.add(functionDeclarationStatement);
		}

		else if (element instanceof FormalParameterListTree) {
			FormalParameterListTree formalParameterList = element
					.asFormalParameterList();
		}
		return statementList;
	}
}
