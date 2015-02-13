package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;

public class FunctionDeclaration extends Function implements SourceElement {
	private String name;
	private boolean isStatic;
	private boolean isGenerator;

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
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

	public FunctionDeclarationTree getFunctionDeclarationTree() {
		return functionDeclaration;
	}

	public void setFunctionDeclarationTree(FunctionDeclarationTree functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}
}