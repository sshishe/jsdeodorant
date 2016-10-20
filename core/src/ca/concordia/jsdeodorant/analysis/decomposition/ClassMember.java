package ca.concordia.jsdeodorant.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public abstract class ClassMember {
	
	private final String name;
	private final ClassDeclaration  owner;
	private final ParseTree parseTree;
	
	public ClassMember(String name, ClassDeclaration owner, ParseTree parseTree) {
		super();
		this.name = name;
		this.owner = owner;
		this.parseTree = parseTree;
	}
	
	public String getName() {
		return name;
	}
	public ClassDeclaration getOwner() {
		return owner;
	}
	
	public ParseTree getParseTree() {
		return parseTree;
	}
	
}
