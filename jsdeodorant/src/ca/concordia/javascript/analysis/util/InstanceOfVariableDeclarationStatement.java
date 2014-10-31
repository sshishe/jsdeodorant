package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

public class InstanceOfVariableDeclarationStatement implements
		ExpressionInstanceChecker {

	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof VariableStatementTree;
	}

}
