package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class ArrayLiteralCreation extends Creation {
	private List<AbstractExpression> elements;

	public ArrayLiteralCreation(List<AbstractExpression> elements) {
		this.elements = elements;
	}

}
