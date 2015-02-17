package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;

import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class FunctionDeclarationStatement extends CompositeStatement {
	public static enum Kind {
		DECLARATION, EXPRESSION, MEMBER, ARROW;
	}
	private String name;
	private Kind kind;
	private List<AbstractExpression> parameters;
	private AbstractExpression expressionContainingFunctionDeclaration;

	public FunctionDeclarationStatement(FunctionDeclarationTree functionDeclarationTree,
			StatementType type, SourceContainer parent) {
		super(functionDeclarationTree, type, parent);
		this.parameters = new ArrayList<>();
		if (functionDeclarationTree.name != null)
			this.name = functionDeclarationTree.name.value;

		this.kind = Kind.valueOf(functionDeclarationTree.kind
				.toString());

		if (functionDeclarationTree.formalParameterList != null) {
			FormalParameterListTree formalParametersList = functionDeclarationTree.formalParameterList
					.asFormalParameterList();
			for (ParseTree parameter : formalParametersList.parameters)
				this.addParameter(new AbstractExpression(parameter));
		}
	}

	public List<AbstractExpression> getParameters() {
		return parameters;
	}

	private void addParameter(AbstractExpression parameter) {
		this.parameters.add(parameter);
	}

	public AbstractExpression getExpressionContainingFunctionDeclaration() {
		return expressionContainingFunctionDeclaration;
	}

	public void setExpressionContainingFunctionDeclaration(
			AbstractExpression expressionContainingFunctionDeclaration) {
		this.expressionContainingFunctionDeclaration = expressionContainingFunctionDeclaration;
	}

}
