package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.FunctionBody;

public class FunctionDeclaration extends SourceElement {

	
	private FunctionBody body; 
	
	protected String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
