package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

public class Program {
	private List<SourceElement> sourceElements;

	public Program() {
		sourceElements = new ArrayList<>();
	}

	public void addSourceElement(SourceElement source) {
		sourceElements.add(source);
	}
}
