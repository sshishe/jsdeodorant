package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.rhino.Node;


public interface VariableDeclarationObject {
	@SuppressWarnings("restriction")
	public Node getVarNode();

	public String getName();
}
