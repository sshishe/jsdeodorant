package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.StatementProcessor;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class FunctionBody {

	private CompositeStatement bodyBlock;

	private AbstractExpression bodyExpression;

	public FunctionBody(BlockTree blockTree) {
		this.bodyBlock = new CompositeStatement(blockTree, StatementType.BLOCK,
				null);
		ImmutableList<ParseTree> statements = blockTree.statements;
		for (ParseTree statement : statements) {
			StatementProcessor.processStatement(statement, bodyBlock);
		}
	}

	public FunctionBody(ParseTree expression) {

	}
}
