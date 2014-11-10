package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

public class Program implements SourceContainer {
	private List<SourceElement> sourceElements;

	public Program() {
		sourceElements = new ArrayList<>();
	}

	public void addSourceElement(SourceElement source) {
		sourceElements.add(source);
	}

	@Override
	public void addElement(SourceElement element) {
		addSourceElement(element);
	}
}
