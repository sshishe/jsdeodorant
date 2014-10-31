package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.FunctionBody;

public class FunctionDeclaration extends SourceElement {

	private FunctionBody body;
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public FunctionBody getBody() {
		return body;
	}

	public void setBody(FunctionBody body) {
		this.body = body;
	}
}
