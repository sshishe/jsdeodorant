package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.GlobalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.LocalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;

public abstract class AbstractFunctionFragment {
	private SourceContainer parent;
	private List<FunctionInvocation> functionInvocationList;
	private List<LocalVariableDeclaration> localVariableDeclarationList;
	private List<GlobalVariableDeclaration> globalVariableDeclarationList;

	protected AbstractFunctionFragment(SourceContainer parent) {
		this.parent = parent;
		functionInvocationList = new ArrayList<>();
		localVariableDeclarationList = new ArrayList<>();
		globalVariableDeclarationList = new ArrayList<>();
	}

	public SourceContainer getParent() {
		return this.parent;
	}

	protected void processFunctionInvocations(
			List<ParseTree> functionInvocations) {
		for (ParseTree functionInvocation : functionInvocations) {
			CallExpressionTree callExpression = (CallExpressionTree) functionInvocation;
			if (callExpression.operand instanceof MemberExpressionTree) {
				MemberExpressionTree operand = (MemberExpressionTree) callExpression.operand;

				List<AbstractExpression> arguments = null;
				if (callExpression.arguments != null) {
					arguments = new ArrayList<>();
					for (ParseTree argument : callExpression.arguments.arguments) {
						arguments.add(new AbstractExpression(argument));
					}

				}
				addFunctionInvocation(new FunctionInvocation(
						operand.memberName.value, new AbstractExpression(
								operand), arguments));
			}

		}
	}

	protected void addFunctionInvocation(FunctionInvocation functionInvocation) {
		functionInvocationList.add(functionInvocation);
		if (parent != null && parent instanceof CompositeStatement) {
			CompositeStatement compositeStatement = (CompositeStatement) parent;
			compositeStatement.addFunctionInvocation(functionInvocation);
		}
	}
}
