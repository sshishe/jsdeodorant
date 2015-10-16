package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.SourceFile;

public class Module {
	private String name;
	private List<String> messages;
	private Program program;
	private SourceFile sourceFile;
	private ModuleType moduleType;
	private List<Module> dependencies;

	public Module(Program program, SourceFile sourceFile, List<String> messages) {
		this.program = program;
		this.sourceFile = sourceFile;
		this.moduleType = ModuleType.File;
		this.messages = messages;
		this.dependencies = new ArrayList<>();
	}

	public Module(String moduleName, ModuleType moduleType, Program program, SourceFile sourceFile, List<String> messages) {
		this.name = moduleName;
		this.moduleType = moduleType;
		this.program = program;
		this.sourceFile = sourceFile;
		this.messages = messages;
		this.dependencies = new ArrayList<>();
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

	public ModuleType getPackageType() {
		return moduleType;
	}

	public void setPackageType(ModuleType packageType) {
		this.moduleType = packageType;
	}

	public List<Module> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Module> dependencies) {
		this.dependencies = dependencies;
	}

	public void addDependency(Module dependency) {
		this.dependencies.add(dependency);
	}

	public void addDependency(List<Module> dependencies) {
		this.dependencies.addAll(dependencies);
	}
}
