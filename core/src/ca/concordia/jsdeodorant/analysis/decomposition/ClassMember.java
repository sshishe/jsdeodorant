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
	
	public boolean  equals(Object o){
		if(!(o instanceof ClassMember)){
			return false;
		}else{
			if(o.hashCode()==this.hashCode()){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	public int hashCode(){
		String id=this.name+this.owner.getName()+this.owner.getParentModule().getSourceFile().getName()+this.parseTree.location;
		return id.hashCode();
	}
	
	
}
