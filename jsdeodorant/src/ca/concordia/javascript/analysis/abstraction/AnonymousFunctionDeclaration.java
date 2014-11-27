package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;

public class AnonymousFunctionDeclaration extends Function {
	private AbstractExpression leftOperand;

	public AnonymousFunctionDeclaration(AbstractExpression leftOperand,
			Function functionDeclaration) {
		this.leftOperand = leftOperand;
		this.setKind(functionDeclaration.getKind());
		this.setParameters(functionDeclaration.getParameters());
		this.setBody(functionDeclaration.getBody());
	}

	public AbstractExpression getLeftOperand() {
		return leftOperand;
	}

	@Override
	public String getName() {
		ParseTree expression = this.leftOperand.getExpression();
		if (expression instanceof IdentifierExpressionTree)
			return expression.asIdentifierExpression().identifierToken.value;
		else if (expression instanceof MemberExpressionTree)
			return expression.asMemberExpression().memberName.value;
		else
			return "";
	}
}
