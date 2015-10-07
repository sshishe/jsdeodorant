package ca.concordia.javascript.analysis.abstraction;

import java.util.List;

import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;

public class Package {
	private String name;
	private Program program;
	private PackageType packageType;
	private List<FunctionDeclaration> functionDeclarations;

	public Package(String name, Program program, PackageType packageType) {
		this.name = name;
		this.program = program;
		this.packageType = packageType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public PackageType getPackageType() {
		return packageType;
	}

	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}

	public void setFunctionDeclarations(List<FunctionDeclaration> functionDeclarations) {
		this.functionDeclarations = functionDeclarations;
	}
}
