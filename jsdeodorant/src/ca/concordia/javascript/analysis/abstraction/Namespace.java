package ca.concordia.javascript.analysis.abstraction;

import java.util.Stack;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.decomposition.ObjectLiteralExpression;

public class Namespace {
	private Namespace parent;
	private AbstractExpression part;

	public Namespace(AbstractExpression part) {
		this.part = part;
	}

	public Namespace getParent() {
		return parent;
	}

	public Namespace setParent(Namespace parent) {
		this.parent = parent;
		return parent;
	}

	public AbstractExpression getPart() {
		return part;
	}

	public void setPart(AbstractExpression part) {
		this.part = part;
	}

	@Override
	public String toString() {
		Namespace currentLevel = this;
		StringBuffer namespaceName = new StringBuffer();
		Stack<String> namespaceStack = new Stack<String>();
		do {
			if (currentLevel.part instanceof ObjectLiteralExpression)
				namespaceStack.push(((ObjectLiteralExpression) currentLevel.part).getName());
			else if (currentLevel.part instanceof FunctionDeclarationExpression)
				namespaceStack.push(((FunctionDeclarationExpression) currentLevel.part).getName());
			if (currentLevel.hasParent())
				namespaceStack.push(".");
			currentLevel = currentLevel.getParent();
		} while (currentLevel != null);

		while (!namespaceStack.isEmpty())
			namespaceName.append(namespaceStack.pop());

		return namespaceName.toString();
	}

	private boolean hasParent() {
		return parent != null;
	}
}
