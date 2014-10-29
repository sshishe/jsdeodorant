package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.*;

public abstract class AbstractFunctionFragment {
	private AbstractFunctionFragment parent;

	private List<LocalVariableDeclarationObject> localVariableDeclarationList;
	private List<GlobalVariableDeclarationObject> globalVariableDeclarationList;

	protected AbstractFunctionFragment(AbstractFunctionFragment parent) {
		this.parent = parent;
		localVariableDeclarationList = new ArrayList<LocalVariableDeclarationObject>();
		globalVariableDeclarationList = new ArrayList<GlobalVariableDeclarationObject>();
	}

	public AbstractFunctionFragment getParent() {
		return this.parent;
	}

	private void addLocalVariableDeclaration(
			LocalVariableDeclarationObject localVariable) {
		localVariableDeclarationList.add(localVariable);
		if (parent != null) {
			parent.addLocalVariableDeclaration(localVariable);
		}
	}

	private void addGlobalVariableDeclaration(
			GlobalVariableDeclarationObject globalVariable) {
		globalVariableDeclarationList.add(globalVariable);
		if (parent != null) {
			parent.addGlobalVariableDeclaration(globalVariable);
		}
	}

	public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
		return localVariableDeclarationList;
	}

	public List<GlobalVariableDeclarationObject> getGlobalVariableDeclarations() {
		return globalVariableDeclarationList;
	}
}
