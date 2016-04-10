package ca.concordia.javascript.analysis.decomposition;

import java.util.Map;
import java.util.TreeMap;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;

public class ClassDeclaration {
	private AbstractIdentifier identifier;
	private FunctionDeclaration functionDeclaration;
	private Map<String, AbstractExpression> methods;
	private Map<String, AbstractExpression> attributes;
	private boolean isInfered;
	private int instantiationCount;

	public ClassDeclaration(AbstractIdentifier identifier, FunctionDeclaration functionDeclaration, boolean isInfered) {
		this.identifier = identifier;
		this.functionDeclaration = functionDeclaration;
		this.attributes = new TreeMap<String, AbstractExpression>();
		this.methods = new TreeMap<String, AbstractExpression>();
		this.isInfered = isInfered;
		instantiationCount = 0;
	}

	public String getName() {
		if (identifier instanceof CompositeIdentifier)
			if (identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("exports") || identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("module.exports"))
				return identifier.asCompositeIdentifier().getRightPart().toString();
		return identifier.toString();
	}

	public void setName(AbstractIdentifier identifier) {
		this.identifier = identifier;
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}

	public void setFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

	public Map<String, AbstractExpression> getMethods() {
		return methods;
	}

	public void addMethod(String name, AbstractExpression expression) {
		this.methods.put(name, expression);
	}

	public void setMethods(Map<String, AbstractExpression> methods) {
		this.methods = methods;
	}

	public Map<String, AbstractExpression> getAttributes() {
		return attributes;
	}

	public void addAttribtue(String name, AbstractExpression expression) {
		this.attributes.put(name, expression);
	}

	public void setAttributes(Map<String, AbstractExpression> attributes) {
		this.attributes = attributes;
	}

	public boolean isInfered() {
		return isInfered;
	}

	public void setInfered(boolean isInfered) {
		this.isInfered = isInfered;
	}

	public int getInstantiationCount() {
		return instantiationCount;
	}

	public void setInstantiationCount(int instantiationCount) {
		this.instantiationCount = instantiationCount;
	}

	public void incrementInstantiationCount() {
		this.instantiationCount++;
	}

}
