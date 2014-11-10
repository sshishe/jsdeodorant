package ca.concordia.javascript.analysis.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.AssignmentRestElementTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.BreakStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.CaseClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.CatchTree;
import com.google.javascript.jscomp.parsing.parser.trees.ClassDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.CommaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionForTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionIfTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyDefinitionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyGetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyMethodTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertySetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ConditionalExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ContinueStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DebuggerStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultParameterTree;
import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.EmptyStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FinallyTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForInStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.GetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MissingPrimaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ModuleImportTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NullTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.PostfixExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;
import com.google.javascript.jscomp.parsing.parser.trees.RestParameterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.SetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.SpreadExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.SuperExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.SwitchStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralPortionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateSubstitutionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TryStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.YieldExpressionTree;

public class ExpressionExtractor {
	ExpressionInstanceChecker instanceChecker;

	public List<ParseTree> getVariableDeclarationExpressions(ParseTree element) {
		instanceChecker = new InstanceOfVariableDeclarationExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getLiteralExpressions(ParseTree element) {
		instanceChecker = new InstanceOfLiteralExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getIdentifierExpressions(ParseTree element) {
		instanceChecker = new InstanceOfIdentifierExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getCallExpressions(ParseTree element) {
		instanceChecker = new InstanceOfCallExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getObjectLiteralExpressions(ParseTree element) {
		instanceChecker = new InstanceOfObjectLiteralExpression();
		return getExpressions(element);
	}
	
	public List<ParseTree> getNewExpressions(ParseTree element) {
		instanceChecker = new InstanceOfNewExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getAssignmentRestExpressions(ParseTree element) {
		instanceChecker = new InstanceOfAssignmentRestExpression();
		return getExpressions(element);
	}

	public List<ParseTree> getBinaryOperators(ParseTree element) {
		instanceChecker = new InstanceOfBinaryOperator();
		return getExpressions(element);
	}
	
	public List<ParseTree> getPostfixExpressions(ParseTree element) {
		instanceChecker = new InstanceOfPostfixExpression();
		return getExpressions(element);
	}
	
	public List<ParseTree> getArrayPatterns(ParseTree element) {
		instanceChecker = new InstanceOfArrayPattern();
		return getExpressions(element);
	}
	
	public List<ParseTree> getCommaExpressions(ParseTree element) {
		instanceChecker = new InstanceOfCommaExpression();
		return getExpressions(element);
	}
	
	public List<ParseTree> getArrayLiteralExpressions(ParseTree element) {
		instanceChecker = new InstanceOfArrayLiteralExpression();
		return getExpressions(element);
	}
	
	public List<ParseTree> getFunctionDeclarations(ParseTree element) {
		instanceChecker = new InstanceOfFunctionDeclaration();
		return getExpressions(element);
	}

	private List<ParseTree> getExpressions(ParseTree element) {
		List<ParseTree> expressionList = new ArrayList<ParseTree>();

		if (element instanceof ProgramTree) {
			ProgramTree program = element.asProgram();

			for (ParseTree sourceElement : program.sourceElements) {
				expressionList.addAll(getExpressions(sourceElement));
			}
		}

		else if (element instanceof BlockTree) {
			BlockTree block = element.asBlock();

			ImmutableList<ParseTree> statements = block.statements;
			for (ParseTree blockStatement : statements) {
				expressionList.addAll(getExpressions(blockStatement));
			}
		}

		else if (element instanceof IfStatementTree) {
			IfStatementTree ifStatement = element.asIfStatement();
			expressionList.addAll(getExpressions(ifStatement.condition));
			expressionList.addAll(getExpressions(ifStatement.ifClause));
			if (ifStatement.elseClause != null)
				expressionList.addAll(getExpressions(ifStatement.elseClause));
		}

		else if (element instanceof WhileStatementTree) {
			WhileStatementTree whileStatement = element.asWhileStatement();
			expressionList.addAll(getExpressions(whileStatement.condition));
			expressionList.addAll(getExpressions(whileStatement.body));
		}

		else if (element instanceof DoWhileStatementTree) {
			DoWhileStatementTree doWhileStatement = element
					.asDoWhileStatement();
			expressionList.addAll(getExpressions(doWhileStatement.condition));
			expressionList.addAll(getExpressions(doWhileStatement.body));
		}

		else if (element instanceof ForInStatementTree) {
			ForInStatementTree forInStatement = element.asForInStatement();
			expressionList.addAll(getExpressions(forInStatement.initializer));
			expressionList.addAll(getExpressions(forInStatement.collection));
			expressionList.addAll(getExpressions(forInStatement.body));
		}

		else if (element instanceof ForStatementTree) {
			ForStatementTree forStatement = element.asForStatement();

			if (forStatement.initializer != null) {
				expressionList.addAll(getExpressions(forStatement.initializer));
			}
			if (forStatement.condition != null) {
				expressionList.addAll(getExpressions(forStatement.condition));
			}
			if (forStatement.increment != null) {
				expressionList.addAll(getExpressions(forStatement.increment));
			}
			expressionList.addAll(getExpressions(forStatement.body));
		}

		else if (element instanceof ForOfStatementTree) {
			ForOfStatementTree forOfStatement = element.asForOfStatement();
			expressionList.addAll(getExpressions(forOfStatement.initializer));
			expressionList.addAll(getExpressions(forOfStatement.collection));
			expressionList.addAll(getExpressions(forOfStatement.body));
		}

		else if (element instanceof WithStatementTree) {
			WithStatementTree withStatement = element.asWithStatement();
			expressionList.addAll(getExpressions(withStatement.expression));
			expressionList.addAll(getExpressions(withStatement.body));
		}

		else if (element instanceof SwitchStatementTree) {
			SwitchStatementTree switchStatement = element.asSwitchStatement();
			expressionList.addAll(getExpressions(switchStatement.expression));
			for (ParseTree caseClause : switchStatement.caseClauses) {
				expressionList.addAll(getExpressions(caseClause));
			}
		}

		else if (element instanceof CaseClauseTree) {
			CaseClauseTree caseClause = element.asCaseClause();
			expressionList.addAll(getExpressions(caseClause.expression));
			for (ParseTree caseClauseStatement : caseClause.statements) {
				expressionList.addAll(getExpressions(caseClauseStatement));
			}
		}

		else if (element instanceof DefaultClauseTree) {
			DefaultClauseTree defaultClause = element.asDefaultClause();
			for (ParseTree defaultClauseStatement : defaultClause.statements) {
				expressionList.addAll(getExpressions(defaultClauseStatement));
			}
		}

		else if (element instanceof TryStatementTree) {
			TryStatementTree tryStatement = element.asTryStatement();
			expressionList.addAll(getExpressions(tryStatement.body));

			if (tryStatement.catchBlock != null) {
				CatchTree catchBlock = tryStatement.catchBlock.asCatch();
				expressionList.addAll(getExpressions(catchBlock.exception));
				expressionList.addAll(getExpressions(catchBlock.catchBody));
			}

			if (tryStatement.finallyBlock != null) {
				FinallyTree finallyBlock = tryStatement.finallyBlock
						.asFinally();
				expressionList.addAll(getExpressions(finallyBlock.block));
			}
		}

		else if (element instanceof LabelledStatementTree) {
			LabelledStatementTree labelledStatement = element
					.asLabelledStatement();
			expressionList.addAll(getExpressions(labelledStatement.statement));
		}

		else if (element instanceof VariableStatementTree) {
			VariableStatementTree variableStatement = element
					.asVariableStatement();
			expressionList
					.addAll(getExpressions(variableStatement.declarations));
		}

		else if (element instanceof VariableDeclarationListTree) {
			VariableDeclarationListTree variableDeclarationList = element
					.asVariableDeclarationList();
			for (VariableDeclarationTree variableDeclaration : variableDeclarationList.declarations) {
				expressionList.addAll(getExpressions(variableDeclaration));
			}
		}

		else if (element instanceof VariableDeclarationTree) {
			VariableDeclarationTree variableDeclaration = element
					.asVariableDeclaration();
			expressionList.addAll(getExpressions(variableDeclaration.lvalue));
			expressionList
					.addAll(getExpressions(variableDeclaration.initializer));

			if (instanceChecker.instanceOf(variableDeclaration))
				expressionList.add(variableDeclaration);
		}

		else if (element instanceof EmptyStatementTree) {
			EmptyStatementTree emptyStatement = element.asEmptyStatement();
		}

		else if (element instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = element
					.asExpressionStatement();
			expressionList
					.addAll(getExpressions(expressionStatement.expression));
		}

		else if (element instanceof BreakStatementTree) {
			BreakStatementTree breakStatement = element.asBreakStatement();
		}

		else if (element instanceof ContinueStatementTree) {
			ContinueStatementTree continueStatement = element
					.asContinueStatement();
		}

		else if (element instanceof ReturnStatementTree) {
			ReturnStatementTree returnStatement = element.asReturnStatement();
			expressionList.addAll(getExpressions(returnStatement.expression));
		}

		else if (element instanceof ThrowStatementTree) {
			ThrowStatementTree thowStatement = element.asThrowStatement();
			expressionList.addAll(getExpressions(thowStatement.value));
		}

		else if (element instanceof DebuggerStatementTree) {
			DebuggerStatementTree debuggerStatement = element
					.asDebuggerStatement();
		}

		else if (element instanceof FunctionDeclarationTree) {
			FunctionDeclarationTree functionDeclarationStatement = element
					.asFunctionDeclaration();
			expressionList
					.addAll(getExpressions(functionDeclarationStatement.formalParameterList));
			expressionList
					.addAll(getExpressions(functionDeclarationStatement.functionBody));
			if (instanceChecker.instanceOf(functionDeclarationStatement))
				expressionList.add(functionDeclarationStatement);
		}

		else if (element instanceof FormalParameterListTree) {
			FormalParameterListTree formalParameterList = element
					.asFormalParameterList();
			for (ParseTree parameter : formalParameterList.parameters) {
				expressionList.addAll(getExpressions(parameter));
			}
		}

		else if (element instanceof ThisExpressionTree) {
			ThisExpressionTree thisExpression = element.asThisExpression();
			if (instanceChecker.instanceOf(thisExpression))
				expressionList.add(thisExpression);
		}

		else if (element instanceof IdentifierExpressionTree) {
			IdentifierExpressionTree identifierExpression = element
					.asIdentifierExpression();
			if (instanceChecker.instanceOf(identifierExpression))
				expressionList.add(identifierExpression);
		}

		else if (element instanceof LiteralExpressionTree) {
			LiteralExpressionTree literalExpression = element
					.asLiteralExpression();
			if (instanceChecker.instanceOf(literalExpression))
				expressionList.add(literalExpression);
		}

		else if (element instanceof ConditionalExpressionTree) {
			ConditionalExpressionTree conditionalExpression = element
					.asConditionalExpression();

			expressionList
					.addAll(getExpressions(conditionalExpression.condition));
			expressionList.addAll(getExpressions(conditionalExpression.left));
			expressionList.addAll(getExpressions(conditionalExpression.right));

			if (instanceChecker.instanceOf(conditionalExpression))
				expressionList.add(conditionalExpression);
		}

		else if (element instanceof BinaryOperatorTree) {
			BinaryOperatorTree binaryOperatorExpression = element
					.asBinaryOperator();

			expressionList
					.addAll(getExpressions(binaryOperatorExpression.left));
			expressionList
					.addAll(getExpressions(binaryOperatorExpression.right));

			if (instanceChecker.instanceOf(binaryOperatorExpression))
				expressionList.add(binaryOperatorExpression);
		}

		else if (element instanceof CallExpressionTree) {
			CallExpressionTree callExpression = element.asCallExpression();

			expressionList.addAll(getExpressions(callExpression.operand));

			expressionList.addAll(getExpressions(callExpression.arguments));

			if (instanceChecker.instanceOf(callExpression))
				expressionList.add(callExpression);
		}

		else if (element instanceof ArgumentListTree) {
			ArgumentListTree argumentList = element.asArgumentList();

			for (ParseTree argument : argumentList.arguments) {
				expressionList.addAll(getExpressions(argument));
			}
		}

		else if (element instanceof MemberExpressionTree) {
			MemberExpressionTree memberExpression = element
					.asMemberExpression();

			expressionList.addAll(getExpressions(memberExpression.operand));

			if (instanceChecker.instanceOf(memberExpression))
				expressionList.add(memberExpression);
		}

		else if (element instanceof PostfixExpressionTree) {
			PostfixExpressionTree postfixExpression = element
					.asPostfixExpression();

			expressionList.addAll(getExpressions(postfixExpression.operand));

			if (instanceChecker.instanceOf(postfixExpression))
				expressionList.add(postfixExpression);
		}

		else if (element instanceof UnaryExpressionTree) {
			UnaryExpressionTree unaryExpression = element.asUnaryExpression();

			expressionList.addAll(getExpressions(unaryExpression.operand));

			if (instanceChecker.instanceOf(unaryExpression))
				expressionList.add(unaryExpression);
		}

		else if (element instanceof AssignmentRestElementTree) {
			AssignmentRestElementTree assignmentRestElement = element
					.asAssignmentRestElement();

			if (instanceChecker.instanceOf(assignmentRestElement))
				expressionList.add(assignmentRestElement);
		}

		else if (element instanceof RestParameterTree) {
			RestParameterTree restParameterElement = element.asRestParameter();

			if (instanceChecker.instanceOf(restParameterElement))
				expressionList.add(restParameterElement);
		}

		else if (element instanceof MemberLookupExpressionTree) {
			MemberLookupExpressionTree memberLookupExpression = element
					.asMemberLookupExpression();

			expressionList
					.addAll(getExpressions(memberLookupExpression.operand));
			expressionList
					.addAll(getExpressions(memberLookupExpression.memberExpression));

			if (instanceChecker.instanceOf(memberLookupExpression))
				expressionList.add(memberLookupExpression);
		}

		else if (element instanceof ParenExpressionTree) {
			ParenExpressionTree parenExpression = element.asParenExpression();

			expressionList.addAll(getExpressions(parenExpression.expression));

			if (instanceChecker.instanceOf(parenExpression))
				expressionList.add(parenExpression);
		}

		else if (element instanceof SuperExpressionTree) {
			SuperExpressionTree superExpression = element.asSuperExpression();

			if (instanceChecker.instanceOf(superExpression))
				expressionList.add(superExpression);
		}

		else if (element instanceof ArrayPatternTree) {
			ArrayPatternTree arrayPatternExpression = element.asArrayPattern();

			for (ParseTree arrayPatternElement : arrayPatternExpression.elements) {
				expressionList.addAll(getExpressions(arrayPatternElement));
			}

			if (instanceChecker.instanceOf(arrayPatternExpression))
				expressionList.add(arrayPatternExpression);
		}

		else if (element instanceof ArrayLiteralExpressionTree) {
			ArrayLiteralExpressionTree arrayLiteralExpression = element
					.asArrayLiteralExpression();

			for (ParseTree arrayLiteralElement : arrayLiteralExpression.elements) {
				expressionList.addAll(getExpressions(arrayLiteralElement));
			}

			if (instanceChecker.instanceOf(arrayLiteralExpression))
				expressionList.add(arrayLiteralExpression);
		}

		else if (element instanceof ObjectPatternTree) {
			ObjectPatternTree objectPatternExpression = element
					.asObjectPattern();

			for (ParseTree objectPatternElement : objectPatternExpression.fields) {
				expressionList.addAll(getExpressions(objectPatternElement));
			}

			if (instanceChecker.instanceOf(objectPatternExpression))
				expressionList.add(objectPatternExpression);
		}

		else if (element instanceof ObjectLiteralExpressionTree) {
			ObjectLiteralExpressionTree objectLiteralExpression = element
					.asObjectLiteralExpression();

			for (ParseTree objectLiteralElement : objectLiteralExpression.propertyNameAndValues) {
				expressionList.addAll(getExpressions(objectLiteralElement));
			}

			if (instanceChecker.instanceOf(objectLiteralExpression))
				expressionList.add(objectLiteralExpression);
		}

		else if (element instanceof NewExpressionTree) {
			NewExpressionTree newExpression = element.asNewExpression();

			expressionList.addAll(getExpressions(newExpression.operand));
			expressionList.addAll(getExpressions(newExpression.arguments));

			if (instanceChecker.instanceOf(newExpression))
				expressionList.add(newExpression);
		}

		else if (element instanceof CommaExpressionTree) {
			CommaExpressionTree commaExpression = element.asCommaExpression();

			for (ParseTree expression : commaExpression.expressions) {
				expressionList.addAll(getExpressions(expression));
			}

			if (instanceChecker.instanceOf(commaExpression))
				expressionList.add(commaExpression);
		}

		else if (element instanceof SpreadExpressionTree) {
			SpreadExpressionTree spreadExpression = element
					.asSpreadExpression();

			expressionList.addAll(getExpressions(spreadExpression.expression));

			if (instanceChecker.instanceOf(spreadExpression))
				expressionList.add(spreadExpression);
		}

		else if (element instanceof MissingPrimaryExpressionTree) {
			MissingPrimaryExpressionTree missingPrimaryExpression = element
					.asMissingPrimaryExpression();

			if (instanceChecker.instanceOf(missingPrimaryExpression))
				expressionList.add(missingPrimaryExpression);
		}

		else if (element instanceof PropertyNameAssignmentTree) {
			PropertyNameAssignmentTree propertyNameAssignmentExpression = element
					.asPropertyNameAssignment();

			if (propertyNameAssignmentExpression.value != null)
				expressionList
						.addAll(getExpressions(propertyNameAssignmentExpression.value));

			if (instanceChecker.instanceOf(propertyNameAssignmentExpression))
				expressionList.add(propertyNameAssignmentExpression);
		}

		else if (element instanceof NullTree) {
			NullTree nullExpression = element.asNull();

			if (instanceChecker.instanceOf(nullExpression))
				expressionList.add(nullExpression);
		}

		else if (element instanceof ClassDeclarationTree) {
			ClassDeclarationTree classDeclarationExpression = element
					.asClassDeclaration();

			if (classDeclarationExpression.superClass != null)
				expressionList
						.addAll(getExpressions(classDeclarationExpression.superClass));

			for (ParseTree expression : classDeclarationExpression.elements) {
				expressionList.addAll(getExpressions(expression));
			}

			if (instanceChecker.instanceOf(classDeclarationExpression))
				expressionList.add(classDeclarationExpression);
		}

		else if (element instanceof YieldExpressionTree) {
			YieldExpressionTree yieldExpression = element.asYieldStatement();

			expressionList.addAll(getExpressions(yieldExpression.expression));

			if (instanceChecker.instanceOf(yieldExpression))
				expressionList.add(yieldExpression);
		}

		else if (element instanceof DefaultParameterTree) {
			DefaultParameterTree defaultParameterExpression = element
					.asDefaultParameter();

			expressionList
					.addAll(getExpressions(defaultParameterExpression.lhs));
			expressionList
					.addAll(getExpressions(defaultParameterExpression.defaultValue));

			if (instanceChecker.instanceOf(defaultParameterExpression))
				expressionList.add(defaultParameterExpression);
		}

		else if (element instanceof GetAccessorTree) {
			GetAccessorTree getAccessorStatement = element.asGetAccessor();

			expressionList.addAll(getExpressions(getAccessorStatement.body));

			if (instanceChecker.instanceOf(getAccessorStatement))
				expressionList.add(getAccessorStatement);
		}

		else if (element instanceof SetAccessorTree) {
			SetAccessorTree setAccessorStatement = element.asSetAccessor();

			expressionList.addAll(getExpressions(setAccessorStatement.body));

			if (instanceChecker.instanceOf(setAccessorStatement))
				expressionList.add(setAccessorStatement);
		}

		else if (element instanceof ComprehensionTree) {
			ComprehensionTree comprehensionExpression = element
					.asComprehension();

			for (ParseTree child : comprehensionExpression.children) {
				expressionList.addAll(getExpressions(child));
			}
			if (comprehensionExpression.tailExpression != null)
				expressionList
						.addAll(getExpressions(comprehensionExpression.tailExpression));

			if (instanceChecker.instanceOf(comprehensionExpression))
				expressionList.add(comprehensionExpression);
		}

		else if (element instanceof ComprehensionIfTree) {
			ComprehensionIfTree comprehensionIfExpression = element
					.asComprehensionIf();

			expressionList
					.addAll(getExpressions(comprehensionIfExpression.expression));

			if (instanceChecker.instanceOf(comprehensionIfExpression))
				expressionList.add(comprehensionIfExpression);
		}

		else if (element instanceof ComprehensionForTree) {
			ComprehensionForTree comprehensionForExpression = element
					.asComprehensionFor();

			expressionList
					.addAll(getExpressions(comprehensionForExpression.initializer));
			expressionList
					.addAll(getExpressions(comprehensionForExpression.collection));

			if (instanceChecker.instanceOf(comprehensionForExpression))
				expressionList.add(comprehensionForExpression);
		}

		else if (element instanceof TemplateLiteralExpressionTree) {
			TemplateLiteralExpressionTree templateLiteralExpression = element
					.asTemplateLiteralExpression();

			expressionList
					.addAll(getExpressions(templateLiteralExpression.operand));

			for (ParseTree templateElement : templateLiteralExpression.elements) {
				expressionList.addAll(getExpressions(templateElement));
			}

			if (instanceChecker.instanceOf(templateLiteralExpression))
				expressionList.add(templateLiteralExpression);
		}

		else if (element instanceof TemplateLiteralPortionTree) {
			TemplateLiteralPortionTree templateLiteralPortionExpression = element
					.asTemplateLiteralPortion();

			if (instanceChecker.instanceOf(templateLiteralPortionExpression))
				expressionList.add(templateLiteralPortionExpression);
		}

		else if (element instanceof TemplateSubstitutionTree) {
			TemplateSubstitutionTree templateSubstitutionExpression = element
					.asTemplateSubstitution();

			expressionList
					.addAll(getExpressions(templateSubstitutionExpression.expression));

			if (instanceChecker.instanceOf(templateSubstitutionExpression))
				expressionList.add(templateSubstitutionExpression);
		}

		else if (element instanceof ComputedPropertyDefinitionTree) {
			ComputedPropertyDefinitionTree computedPropertyDefinitionExpression = element
					.asComputedPropertyDefinition();

			expressionList
					.addAll(getExpressions(computedPropertyDefinitionExpression.property));

			expressionList
					.addAll(getExpressions(computedPropertyDefinitionExpression.value));

			if (instanceChecker
					.instanceOf(computedPropertyDefinitionExpression))
				expressionList.add(computedPropertyDefinitionExpression);
		}

		else if (element instanceof ComputedPropertyMethodTree) {
			ComputedPropertyMethodTree computedPropertyMethodDefinitionExpression = element
					.asComputedPropertyMethod();

			expressionList
					.addAll(getExpressions(computedPropertyMethodDefinitionExpression.property));

			expressionList
					.addAll(getExpressions(computedPropertyMethodDefinitionExpression.method));

			if (instanceChecker
					.instanceOf(computedPropertyMethodDefinitionExpression))
				expressionList.add(computedPropertyMethodDefinitionExpression);
		}

		else if (element instanceof ComputedPropertyGetterTree) {
			ComputedPropertyGetterTree computedPropertyGetterDefinitionExpression = element
					.asComputedPropertyGetter();

			expressionList
					.addAll(getExpressions(computedPropertyGetterDefinitionExpression.property));

			expressionList
					.addAll(getExpressions(computedPropertyGetterDefinitionExpression.body));

			if (instanceChecker
					.instanceOf(computedPropertyGetterDefinitionExpression))
				expressionList.add(computedPropertyGetterDefinitionExpression);
		}

		else if (element instanceof ComputedPropertySetterTree) {
			ComputedPropertySetterTree computedPropertySetterDefinitionExpression = element
					.asComputedPropertySetter();

			expressionList
					.addAll(getExpressions(computedPropertySetterDefinitionExpression.property));

			expressionList
					.addAll(getExpressions(computedPropertySetterDefinitionExpression.body));

			if (instanceChecker
					.instanceOf(computedPropertySetterDefinitionExpression))
				expressionList.add(computedPropertySetterDefinitionExpression);
		}

		else if (element instanceof ModuleImportTree) {
			ModuleImportTree moduleImportExpression = element.asModuleImport();

			if (instanceChecker.instanceOf(moduleImportExpression))
				expressionList.add(moduleImportExpression);
		}

		else if (element instanceof ExportDeclarationTree) {
			ExportDeclarationTree exportDeclarationExpression = element
					.asExportDeclaration();

			expressionList
					.addAll(getExpressions(exportDeclarationExpression.declaration));

			for (ParseTree exportDeclaration : exportDeclarationExpression.exportSpecifierList) {
				expressionList.addAll(getExpressions(exportDeclaration));
			}

			if (instanceChecker.instanceOf(exportDeclarationExpression))
				expressionList.add(exportDeclarationExpression);
		}

		else if (element instanceof ExportSpecifierTree) {
			ExportSpecifierTree exportSpecifierExpression = element
					.asExportSpecifier();

			if (instanceChecker.instanceOf(exportSpecifierExpression))
				expressionList.add(exportSpecifierExpression);
		}

		else if (element instanceof ImportDeclarationTree) {
			ImportDeclarationTree importDeclarationExpression = element
					.asImportDeclaration();

			for (ParseTree importDeclaration : importDeclarationExpression.importSpecifierList) {
				expressionList.addAll(getExpressions(importDeclaration));
			}

			if (instanceChecker.instanceOf(importDeclarationExpression))
				expressionList.add(importDeclarationExpression);
		}

		else if (element instanceof ImportSpecifierTree) {
			ImportSpecifierTree importSpecifierExpression = element
					.asImportSpecifier();

			if (instanceChecker.instanceOf(importSpecifierExpression))
				expressionList.add(importSpecifierExpression);
		}

		return expressionList;
	}
}
