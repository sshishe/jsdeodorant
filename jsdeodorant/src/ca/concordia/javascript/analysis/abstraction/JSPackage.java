package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.SourceFile;

public class JSPackage {
	private String name;
	private List<String> messages;
	private Program program;
	private SourceFile sourceFile;
	private PackageType packageType;

	public JSPackage(Program program, SourceFile sourceFile, List<String> messages) {
		this.program = program;
		this.sourceFile = sourceFile;
		messages = new ArrayList<>();
	}

	public JSPackage(String packageName, PackageType packageType, Program program, SourceFile sourceFile, List<String> messages) {
		this.name = packageName;
		this.packageType = packageType;
		this.program = program;
		this.sourceFile = sourceFile;
		messages = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMessages() {
		return messages;
	}

	public Program getProgram() {
		return program;
	}

	public SourceFile getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	public PackageType getPackageType() {
		return packageType;
	}

	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}
}
