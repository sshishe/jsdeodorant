package ca.concordia.jsdeodorant.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public abstract class TypeMember {
	
	private final String name;
	private final TypeDeclaration  owner;
	private final ParseTree parseTree;
	
	public TypeMember(String name, TypeDeclaration owner, ParseTree parseTree) {
		super();
		this.name = name;
		this.owner = owner;
		this.parseTree = parseTree;
	}
	
	public String getName() {
		return name;
	}
	public TypeDeclaration getOwner() {
		return owner;
	}
	
	public ParseTree getParseTree() {
		return parseTree;
	}
	
	public boolean  equals(Object o){
		if(!(o instanceof TypeMember)){
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
