package ca.concordia.jsdeodorant.analysis.abstraction;

import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;

public class Dependency {
	private String name;
	private AbstractExpression expresion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractExpression getExpresion() {
		return expresion;
	}

	public Dependency(String name, AbstractExpression expresion, Module dependency) {
		this.name = name;
		this.expresion = expresion;
		this.dependency = dependency;
	}

	public void setExpresion(AbstractExpression expresion) {
		this.expresion = expresion;
	}

	public Module getDependency() {
		return dependency;
	}

	public void setDependency(Module dependency) {
		this.dependency = dependency;
	}

	private Module dependency;
}
