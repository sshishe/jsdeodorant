package ca.concordia.javascript.analysis.abstraction;

public abstract class FunctionDeclarationObject {

	protected String name;

	public abstract FunctionDeclarationObject getFunctionObject();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
