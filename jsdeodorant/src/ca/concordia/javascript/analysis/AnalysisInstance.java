package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.SourceFile;

import ca.concordia.javascript.analysis.abstraction.Program;

public class AnalysisInstance {
	private List<String> messages;
	private Program program;
	private SourceFile sourceFile;

	public AnalysisInstance(Program program, SourceFile sourceFile, List<String> messages) {
		this.program = program;
		this.sourceFile = sourceFile;
		messages = new ArrayList<>();
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
}
