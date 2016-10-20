package ca.concordia.jsdeodorant.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class Attribute extends ClassMember{

	public Attribute(String name, ClassDeclaration owner, ParseTree parseTree) {
		super(name, owner, parseTree);
	}

}
