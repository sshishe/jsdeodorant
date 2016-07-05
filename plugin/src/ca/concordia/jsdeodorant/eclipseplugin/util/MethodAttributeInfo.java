package ca.concordia.jsdeodorant.eclipseplugin.util;

import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;

public class MethodAttributeInfo {
	
	public enum Type {
		METHOD("Method"), 
		ATTRIBUTE("Attribute");
		
		private String name;

		Type(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	private final String name;
	private final AbstractExpression abstractExpression;
	private final Type type;
	private final ClassDeclaration parentClassDeclaration;
	
	public MethodAttributeInfo(String name, AbstractExpression abstractExpression, ClassDeclaration parentClassDeclaration, Type type) {
		this.name = name;
		this.abstractExpression = abstractExpression;
		this.parentClassDeclaration = parentClassDeclaration;
		this.type = type;
	}

	public String getName() {
		if (type == Type.METHOD)
			return name + "()";
		return name;
	}

	public AbstractExpression getAbstractExpression() {
		return abstractExpression;
	}

	public Type getType() {
		return type;
	}

	public ClassDeclaration getParentClassDeclaration() {
		return parentClassDeclaration;
	}
	
	@Override
	public String toString() {
		return type + ": " + name;
	}
	
}
