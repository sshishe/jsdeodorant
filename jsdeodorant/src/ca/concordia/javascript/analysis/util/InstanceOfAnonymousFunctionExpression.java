package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;

public class InstanceOfAnonymousFunctionExpression implements ExpressionInstanceChecker {
	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof BinaryOperatorTree | expression instanceof VariableDeclarationTree;
	}
}
