package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionBody;

public abstract class Function {

	public static enum Kind {
		DECLARATION, EXPRESSION, MEMBER, ARROW
	}

	
	protected FunctionBody body;
	protected Kind kind;
	protected List<AbstractExpression> parameters;
	
	public Function() {
		parameters = new ArrayList<>();
	}

	public FunctionBody getBody() {
		return body;
	}

	public void setBody(FunctionBody body) {
		this.body = body;
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
	//pull up common attributes from FunctionDeclaration and AnonymousFunctionDeclaration

	public abstract String getName();
	public abstract FunctionDeclarationTree getFunctionDeclarationTree();
	public abstract void setFunctionDeclarationTree(FunctionDeclarationTree functionDeclaration);
}
