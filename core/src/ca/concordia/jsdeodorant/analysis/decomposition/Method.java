package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.EnumSet;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

public class Method {
	
	private String name;
	private FunctionDeclarationTree functionDeclarationTree;
	//private FunctionDeclaration functionDeclaration;
	private EnumSet<MethodType> kinds;
	
	public Method(String name, FunctionDeclarationTree functionDeclarationTree,//FunctionDeclaration functionDeclaration,
			EnumSet<MethodType> kinds) {

		this.name = name;
		this.functionDeclarationTree = functionDeclarationTree;
		//this.functionDeclaration=functionDeclaration;
		this.kinds = kinds;
	}

	public String getName() {
		return name;
	}

	public FunctionDeclarationTree getFunctionDeclarationTree() {
		return functionDeclarationTree;
	}

	public EnumSet<MethodType> getKinds() {
		return kinds;
	}
	
//	public FunctionDeclaration getFunctionDeclaration(){
//		return this.functionDeclaration;
//	}
	
	
}
