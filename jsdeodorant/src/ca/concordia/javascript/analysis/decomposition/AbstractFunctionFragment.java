package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.GlobalVariableDeclaration;
import ca.concordia.javascript.analysis.abstraction.LocalVariableDeclaration;

public abstract class AbstractFunctionFragment {
	private AbstractFunctionFragment parent;
	private List<FunctionInvocation> functionInvocationList;
	private List<LocalVariableDeclaration> localVariableDeclarationList;
	private List<GlobalVariableDeclaration> globalVariableDeclarationList;

	protected AbstractFunctionFragment(AbstractFunctionFragment parent) {
		this.parent = parent;
		functionInvocationList = new ArrayList<>();
		localVariableDeclarationList = new ArrayList<>();
		globalVariableDeclarationList = new ArrayList<>();
	}

	public AbstractFunctionFragment getParent() {
		return this.parent;
	}
}
