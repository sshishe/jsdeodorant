package ca.concordia.javascript.analysis.abstraction;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class Export {
	private String name;
	private AbstractExpression rValue;

	public Export(String name, AbstractExpression rValue) {
		this.name = name;
		this.rValue = rValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractExpression getRValue() {
		return rValue;
	}

	public void setrValue(AbstractExpression rValue) {
		this.rValue = rValue;
	}
}
