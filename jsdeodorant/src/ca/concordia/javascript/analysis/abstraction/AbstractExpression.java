package ca.concordia.javascript.analysis.abstraction;


import ca.concordia.javascript.analysis.decomposition.*;


public class AbstractExpression extends AbstractFunctionFragment {

	
	private ASTInformation expression;
	
	protected AbstractExpression(/*Expression parent*/) {
		super(null);
		// TODO Auto-generated constructor stub
	}
	
	protected AbstractExpression(AbstractFunctionFragment parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

}
