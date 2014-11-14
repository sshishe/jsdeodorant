package ca.concordia.javascript.analysis.abstraction;

import java.util.Map;
import java.util.Set;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class ObjectLiteralCreation extends Creation {
	private Map<String, AbstractExpression> propertyNameAndValueMap;
	
	public ObjectLiteralCreation(Map<String, AbstractExpression> propertyNameAndValues) {
		this.propertyNameAndValueMap = propertyNameAndValues;
	}
	
	public Set<String> getPropertyNames() {
		return propertyNameAndValueMap.keySet();
	}
}
