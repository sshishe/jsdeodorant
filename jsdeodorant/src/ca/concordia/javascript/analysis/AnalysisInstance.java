package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.Program;

public class AnalysisInstance {
	private List<String> messages;
	private Program program;

	public AnalysisInstance(Program program, List<String> messages) {
		this.program = program;
		messages = new ArrayList<>();
	}

	public List<String> getMessages() {
		return messages;
	}

	public Program getProgram() {
		return program;
	}
}
