package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;

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

	public List<AbstractExpression> getAssignments();
	
	public AbstractIdentifier getRawIdentifier();
}
