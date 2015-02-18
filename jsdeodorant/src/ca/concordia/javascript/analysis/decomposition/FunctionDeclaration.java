package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

public interface FunctionDeclaration {
	public String getName();
	public FunctionKind getKind();
	public List<AbstractExpression> getParameters();
	public FunctionDeclarationTree getFunctionDeclarationTree();
}
