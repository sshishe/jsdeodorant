package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class ArrayLiteralCreation extends Creation {
	private List<AbstractExpression> elements;

	public ArrayLiteralCreation(List<AbstractExpression> elements) {
		this.elements = elements;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ArrayLiteralCreation) {
			ArrayLiteralCreation toCompare = (ArrayLiteralCreation) other;
			return Objects.deepEquals(this.elements, toCompare.elements);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(elements);
	}
}
