package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionBody;

public class FunctionDeclaration implements SourceElement {

	public static enum Kind {
		DECLARATION, EXPRESSION, MEMBER, ARROW
	}

	private FunctionBody body;
	private String name;
	private boolean isStatic;
	private boolean isGenerator;
	private Kind kind;
	private List<AbstractExpression> parameters;

	public FunctionDeclaration() {
		parameters = new ArrayList<>();
	}

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

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isGenerator() {
		return isGenerator;
	}

	public void setGenerator(boolean isGenerator) {
		this.isGenerator = isGenerator;
	}

	public List<AbstractExpression> getParameters() {
		return parameters;
	}

	public void addParameter(AbstractExpression parameter) {
		this.parameters.add(parameter);
	}

	public void setParameters(List<AbstractExpression> parameters) {
		this.parameters = parameters;
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}
}
