package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;

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

	public List<SourceElement> getSourceElements() {
		return sourceElements;
	}

	public List<ObjectCreation> getObjectCreations() {
		List<ObjectCreation> objectCreations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (Creation creation : abstractFunctionFragment
						.getCreations())
					if (creation instanceof ObjectCreation)
						objectCreations.add((ObjectCreation) creation);
			}
		}
		return objectCreations;
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (FunctionDeclaration functionDeclaration : abstractFunctionFragment
						.getFuntionDeclarations())
					functionDeclarations.add(functionDeclaration);
			} else if (sourceElement instanceof FunctionDeclaration)
				functionDeclarations.add((FunctionDeclaration) sourceElement);
		}
		return functionDeclarations;
	}
}
