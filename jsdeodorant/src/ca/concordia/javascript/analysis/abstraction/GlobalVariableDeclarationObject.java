package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.rhino.Node;


public class GlobalVariableDeclarationObject implements
		VariableDeclarationObject {

	private String name;

	@Override
	public Node getVarNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

}
