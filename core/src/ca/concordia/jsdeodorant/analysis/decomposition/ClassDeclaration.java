package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.Map;
import java.util.TreeMap;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;

public class ClassDeclaration {
	private AbstractIdentifier identifier;
	private FunctionDeclaration functionDeclaration;
	private Map<String, AbstractExpression> methods;
	private Map<String, AbstractExpression> attributes;
	private boolean isInfered;
	private boolean hasNamespace;
	private int instantiationCount;
	private LibraryType libraryType;
	private boolean isAliased;
	// If method defines outside of the constructor, then keep LOC
	private int extraMethodLines;

	// After inferring the class, we try to resolve the corresponding 'new' which we were not able to matched before.
	private boolean matchedAfterInference;
	private Module parentModule;

	public ClassDeclaration(AbstractIdentifier identifier, FunctionDeclaration functionDeclaration, boolean isInfered, boolean hasNamespace, LibraryType libraryType, boolean isAliased, Module parentModule) {
		this.identifier = identifier;
		this.functionDeclaration = functionDeclaration;
		this.setParentModule(parentModule);
		this.attributes = new TreeMap<String, AbstractExpression>();
		this.methods = new TreeMap<String, AbstractExpression>();
		this.isInfered = isInfered;
		this.hasNamespace = hasNamespace;
		this.instantiationCount = 0;
		this.libraryType = libraryType;
		this.isAliased = isAliased;
	}

	public String getName() {
		//		if (identifier instanceof CompositeIdentifier)
		//			if (identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("exports") || identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("module.exports"))
		//				return identifier.asCompositeIdentifier().getRightPart().toString();
		//		return identifier.toString();
		//return functionDeclaration.getRawIdentifier().toString();
		return functionDeclaration.getName().toString();
	}

	public AbstractIdentifier getRawIdentifier() {
		return functionDeclaration.getRawIdentifier();
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

	public void addMethod(String name, AbstractExpression expression, int locToBeAdded) {
		this.extraMethodLines += locToBeAdded;
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

	public boolean hasNamespace() {
		return hasNamespace;
	}

	public void setHasNamespace(boolean hasNamespace) {
		this.hasNamespace = hasNamespace;
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(LibraryType libraryType) {
		this.libraryType = libraryType;
	}

	public boolean isAliased() {
		return isAliased;
	}

	public void setAliased(boolean isAliased) {
		this.isAliased = isAliased;
	}

	public boolean isMatchedAfterInference() {
		return matchedAfterInference;
	}

	public void setMatchedAfterInference(boolean matchedAfterInference) {
		this.matchedAfterInference = matchedAfterInference;
	}

	public int getExtraMethodLines() {
		if (extraMethodLines < 0) {
			extraMethodLines = 0;
		}
		return extraMethodLines;
	}

	public void setExtraMethodLines(int extraMethodLines) {
		this.extraMethodLines = extraMethodLines;
	}

	public Module getParentModule() {
		return parentModule;
	}

	public void setParentModule(Module parentModule) {
		this.parentModule = parentModule;
	}

}
