package ca.concordia.javascript.analysis.decomposition;

import java.util.List;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

public interface FunctionDeclaration {
	public String getName();

	public String getQualifiedName();

	public AbstractIdentifier getIdentifier();

	public FunctionKind getKind();

	public List<AbstractExpression> getParameters();

	public FunctionDeclarationTree getFunctionDeclarationTree();

	public List<AbstractStatement> getReturnStatementList();

	public List<AbstractStatement> getStatements();

	public boolean isClassDeclaration();

	public void setClassDeclaration(boolean state);
}
