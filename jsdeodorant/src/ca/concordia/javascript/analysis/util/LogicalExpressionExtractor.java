package ca.concordia.javascript.analysis.util;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class LogicalExpressionExtractor {
	private LogicalExpressionInstanceChecker instanceChecker;
	private ExpressionExtractor expressionExtractor;

	public LogicalExpressionExtractor() {
		expressionExtractor = new ExpressionExtractor();
	}

	public List<ParseTree> getAndExpressions(ParseTree expression) {
		instanceChecker = new InstanceOfAndOperator();
		return getLogicalExpressions(expression);
	}

	public List<ParseTree> getOrExpressions(ParseTree expression) {
		instanceChecker = new InstanceOfOrOperator();
		return getLogicalExpressions(expression);
	}

	private List<ParseTree> getLogicalExpressions(ParseTree expression) {
		List<ParseTree> expressionList = new ArrayList<ParseTree>();
		if (instanceChecker.instanceOf(expression))
			expressionList.add(expression);

		for (ParseTree extractedExpression : expressionExtractor
				.getParseTree(expression))
			if (extractedExpression != expression)
				expressionList
						.addAll(getLogicalExpressions(extractedExpression));

		return expressionList;
	}
}
