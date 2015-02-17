package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.SourceHelper;

public abstract class Function {

	public static enum Kind {
		DECLARATION, EXPRESSION, MEMBER, ARROW
	}

	protected Kind kind;
	protected List<AbstractExpression> parameters;
	protected FunctionDeclarationTree functionDeclaration;

	public Function() {
		parameters = new ArrayList<>();
	}

	public List<AbstractExpression> getParameters() {
		return parameters;
	}

	public void addParameter(AbstractExpression parameter) {
		this.parameters.add(parameter);
	}

	public void setParameters(List<AbstractExpression> parameters) {
		this.parameters = parameters;
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		return SourceHelper.extract(functionDeclaration);
	}

	// pull up common attributes from FunctionDeclaration and
	// AnonymousFunctionDeclaration

	public abstract String getName();

	public abstract FunctionDeclarationTree getFunctionDeclarationTree();

	public abstract void setFunctionDeclarationTree(
			FunctionDeclarationTree functionDeclaration);
}
