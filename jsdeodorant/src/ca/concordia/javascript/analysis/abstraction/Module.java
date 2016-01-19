package ca.concordia.javascript.analysis.abstraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.javascript.jscomp.SourceFile;

import ca.concordia.javascript.analysis.module.LibraryType;

public class Module {
	private List<String> messages;
	private Program program;
	private SourceFile sourceFile;
	private ModuleType moduleType;
	private LibraryType libraryType;
	@Override
	public String toString() {
		return "Module [sourceFile=" + sourceFile + ", moduleType=" + moduleType + ", libraryType=" + libraryType + "]";
	}

	private Map<String, Module> dependencies;
	private List<Export> exports;

	public Module(Program program, SourceFile sourceFile, List<String> messages) {
		this.program = program;
		this.sourceFile = sourceFile;
		this.moduleType = ModuleType.File;
		this.messages = messages;
		this.dependencies = new HashMap<>();
		this.exports = new ArrayList<>();
		this.libraryType = LibraryType.NONE;
	}

	public Module(ModuleType moduleType, Program program, SourceFile sourceFile, List<String> messages) {
		this.moduleType = moduleType;
		this.program = program;
		this.sourceFile = sourceFile;
		this.messages = messages;
		this.dependencies = new HashMap<>();
		this.exports = new ArrayList<>();
		this.libraryType = LibraryType.NONE;
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

	public Map<String, Module> getDependencies() {
		return dependencies;
	}

	public void addDependency(String name, Module dependency) {
		this.dependencies.put(name, dependency);
	}

	public List<Export> getExports() {
		return exports;
	}

	public void addExport(Export export) {
		exports.add(export);
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public void setAsLibrary(LibraryType libraryType) {
		this.libraryType = libraryType;
	}

	public String getCanonicalPath() {
		try {
			return new File(this.getSourceFile().getOriginalPath()).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
