package ca.concordia.jsdeodorant.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Attribute extends TypeMember{

	public Attribute(String name, TypeDeclaration owner, ParseTree parseTree) {
		super(name, owner, parseTree);
	}

}
