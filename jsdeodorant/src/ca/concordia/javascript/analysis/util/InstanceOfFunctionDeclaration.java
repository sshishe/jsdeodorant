package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class InstanceOfFunctionDeclaration implements ExpressionInstanceChecker {

	@Override
	public boolean instanceOf(ParseTree expression) {
		return expression instanceof FunctionDeclarationTree;
	}

}
