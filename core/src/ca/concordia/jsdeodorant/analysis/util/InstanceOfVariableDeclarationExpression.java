package ca.concordia.jsdeodorant.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;

public class InstanceOfVariableDeclarationExpression implements
		ExpressionInstanceChecker {

	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof VariableDeclarationTree;
	}

}
