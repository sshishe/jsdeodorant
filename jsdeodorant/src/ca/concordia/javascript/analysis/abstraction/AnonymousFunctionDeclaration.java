package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.QualifiedNameExtractor;

public class AnonymousFunctionDeclaration extends Function implements
		SourceElement {
	private AbstractExpression leftOperand;

	public AnonymousFunctionDeclaration(AbstractExpression leftOperand,
			Function functionDeclaration) {
		this.leftOperand = leftOperand;
		this.setKind(functionDeclaration.getKind());
		this.setParameters(functionDeclaration.getParameters());
		//this.setBody(functionDeclaration.getBody());
	}

	public AbstractExpression getLeftOperand() {
		return leftOperand;
	}

	public FunctionDeclarationTree getFunctionDeclarationTree() {
		return functionDeclaration;
	}

	public void setFunctionDeclarationTree(
			FunctionDeclarationTree functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

	@Override
	public String getName() {
		return QualifiedNameExtractor.getQualifiedName(
				this.leftOperand.getExpression()).toString();
	}
}
