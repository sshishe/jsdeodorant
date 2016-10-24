package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class ClassDeclaration {
	private AbstractIdentifier identifier;
	private FunctionDeclaration functionDeclaration;
	private Set<ClassMember> classMembers;
	private boolean isInfered;
	private InferenceType inferenceType;
	private boolean hasNamespace;
	private int instantiationCount;
	private LibraryType libraryType;
	private boolean isAliased;
	// If method defines outside of the constructor, then keep LOC
	private int extraMethodLines;
	private ClassDeclaration superType; // Thankfully JavasScript supports single inheritance 
	private Vector<ClassDeclaration> subTypes;
	public ClassDeclaration getSuperType() {
		return superType;
	}

	public void setSuperType(ClassDeclaration superType) {
		this.superType = superType;
	}

	private Set<FunctionDeclaration> constructors;// transpiled code from typeScript has constructors
	private boolean hasConstructor;

	// After inferring the class, we try to resolve the corresponding 'new' which we were not able to matched before.
	private boolean matchedAfterInference;
	private Module parentModule;

	public ClassDeclaration(AbstractIdentifier identifier, FunctionDeclaration functionDeclaration, boolean isInfered, boolean hasNamespace, LibraryType libraryType, boolean isAliased, Module parentModule) {
		this.identifier = identifier;
		this.functionDeclaration = functionDeclaration;
		this.setParentModule(parentModule);
		this.classMembers= new HashSet<ClassMember>();
		this.isInfered = isInfered;
		this.hasNamespace = hasNamespace;
		this.instantiationCount = 0;
		this.libraryType = libraryType;
		this.isAliased = isAliased;
		this.constructors= new HashSet<FunctionDeclaration>();
		this.superType=null;
		this.subTypes= new Vector<ClassDeclaration>();
	}

	public Vector<ClassDeclaration> getSubTypes() {
		return subTypes;
	}
	
	public Set<ClassMember> getClassMembers() {
		return classMembers;
	}

	public void addToSubTypes(ClassDeclaration aSubType) {
		if(this.subTypes.size()==0){
			this.subTypes.add(aSubType);
		}else{
			boolean exist=false;
			for(ClassDeclaration sub: this.subTypes){
				if(sub.getFunctionDeclaration().equals(aSubType.getFunctionDeclaration())){
					exist=true;
				}
			}
			if(!exist){
				this.subTypes.add(aSubType);
			}
		}
		
	}

	public String getName() {
		//		if (identifier instanceof CompositeIdentifier)
		//			if (identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("exports") || identifier.asCompositeIdentifier().getMostLeftPart().toString().equals("module.exports"))
		//				return identifier.asCompositeIdentifier().getRightPart().toString();
		//		return identifier.toString();
		//return functionDeclaration.getRawIdentifier().toString();
		return functionDeclaration.getName().toString();
	}

	public AbstractIdentifier getRawIdentifier() {
		return functionDeclaration.getRawIdentifier();
	}

	public void setName(AbstractIdentifier identifier) {
		this.identifier = identifier;
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}

	public void setFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

	public boolean isInfered() {
		return isInfered;
	}
	

	public InferenceType getInferenceType() {
		return inferenceType;
	}

	public void setInferenceType(InferenceType inferenceType) {
		this.inferenceType = inferenceType;
	}

	public void setInfered(boolean isInfered) {
		this.isInfered = isInfered;
	}

	public int getInstantiationCount() {
		return instantiationCount;
	}

	public void setInstantiationCount(int instantiationCount) {
		this.instantiationCount = instantiationCount;
	}

	public void incrementInstantiationCount() {
		this.instantiationCount++;
	}

	public boolean hasNamespace() {
		return hasNamespace;
	}

	public void setHasNamespace(boolean hasNamespace) {
		this.hasNamespace = hasNamespace;
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(LibraryType libraryType) {
		this.libraryType = libraryType;
	}

	public boolean isAliased() {
		return isAliased;
	}

	public void setAliased(boolean isAliased) {
		this.isAliased = isAliased;
	}

	public boolean isMatchedAfterInference() {
		return matchedAfterInference;
	}

	public void setMatchedAfterInference(boolean matchedAfterInference) {
		this.matchedAfterInference = matchedAfterInference;
	}

	public int getExtraMethodLines() {
		if (extraMethodLines < 0) {
			extraMethodLines = 0;
		}
		return extraMethodLines;
	}

	public void setExtraMethodLines(int extraMethodLines) {
		this.extraMethodLines = extraMethodLines;
	}

	public Module getParentModule() {
		return parentModule;
	}

	public void setParentModule(Module parentModule) {
		this.parentModule = parentModule;
	}


	public boolean hasConstructor() {
		return hasConstructor;
	}

	public void setHasConstrucotr(boolean hasConstrucotr) {
		this.hasConstructor = hasConstrucotr;
	}

	public Set<FunctionDeclaration> getConstructors() {
		return constructors;
	}
	
	
	
	// it only identify attributes within the body of the constructor or class (not in the functions and methods belong to the class)
	public void identifyAttributes(){
		if(this.hasConstructor()){
			for(FunctionDeclaration constructor:this.constructors ){
				AbstractStatement body=constructor.getStatements().get(0);
				this.identifyAttributes(((CompositeStatement)body).getStatements());
			}
		}else{
			AbstractStatement body=this.functionDeclaration.getStatements().get(0);
			this.identifyAttributes(((CompositeStatement)body).getStatements());
		}
	}
	
	private void identifyAttributes(List<AbstractStatement> statements){

		for(AbstractStatement abstractStatement: statements){	
			ParseTree parseTree=abstractStatement.getStatement();
			if(abstractStatement instanceof Statement){
				if(parseTree instanceof ExpressionStatementTree){
					ExpressionStatementTree expressionStatementTree=parseTree.asExpressionStatement();
					if(expressionStatementTree.expression instanceof BinaryOperatorTree){ // A=B;
						ParseTree left=expressionStatementTree.expression.asBinaryOperator().left;
						ParseTree right=expressionStatementTree.expression.asBinaryOperator().right;
						if(right instanceof FunctionDeclarationTree){ //A=function(){}; or 
							//System.out.println("\t\t\t  right instanceof FunctionDeclarationTree");
						}else{ // we are looking for attribute thus right should not be FunctionDeclarationTree
							AbstractIdentifier leftId=IdentifierHelper.getIdentifier(left);
							if(leftId instanceof CompositeIdentifier){
								String leftIdAsString=leftId.asCompositeIdentifier().toString();
								if(leftIdAsString.startsWith("this.")){
									if(!(leftIdAsString.split(".").length>2)){ 
										// more strict rules only one dot => this.SOMETHING and NOT  this.SOMETHING.OTHERTHINGS
										Attribute attr=new Attribute(((CompositeIdentifier) leftId).getMostRightPart().toString(), this,parseTree);
										this.classMembers.add(attr);
									}
								}
								
							}else{
								//we don't care as it should start with "this."
							}
						}
					}
				}
			}else if(abstractStatement instanceof CompositeStatement){
				if(!(abstractStatement.getStatement() instanceof FunctionDeclarationTree) &&  !(abstractStatement.getStatement() instanceof LabelledStatementTree)){ // we don't go inside functions or object literals
					identifyAttributes(((CompositeStatement)abstractStatement).getStatements());		
				}
			}
		}	
	}
	
	public void identifyMethodsWithinClassBody(){
		AbstractStatement body=this.functionDeclaration.getStatements().get(0);
		this.identifyMethodsWithinClassBody(((CompositeStatement)body).getStatements());
	}
	
	private void identifyMethodsWithinClassBody(List<AbstractStatement> statements){
		
		for(AbstractStatement abstractStatement: statements){	
			ParseTree parseTree=abstractStatement.getStatement();
			if(abstractStatement instanceof Statement){
				if(parseTree instanceof ExpressionStatementTree){
					ExpressionStatementTree expressionStatementTree=parseTree.asExpressionStatement();
					if(expressionStatementTree.expression instanceof BinaryOperatorTree){ // A=B;
						ParseTree left=expressionStatementTree.expression.asBinaryOperator().left;
						ParseTree right=expressionStatementTree.expression.asBinaryOperator().right;
						if(right instanceof FunctionDeclarationTree){ //A=function(){}; or 
							AbstractIdentifier leftId=IdentifierHelper.getIdentifier(left);
							if(leftId instanceof CompositeIdentifier){
								String leftIdAsString=leftId.asCompositeIdentifier().toString();
								if(leftIdAsString.startsWith("this.") || leftIdAsString.startsWith(this.getName()+".prototype.")){
									if(leftIdAsString.startsWith("this.") ){
										EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredWithinClassBody);
										Method aMethod= new Method(leftIdAsString.replace("this.", ""), this,right.asFunctionDeclaration(), kinds);
										this.classMembers.add(aMethod);
									}else if(leftIdAsString.startsWith(this.getName()+".prototype.")){
										EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredWithinClassBody);
										Method aMethod= new Method(leftIdAsString.replace(this.getName()+".prototype.", ""), this,right.asFunctionDeclaration(), kinds);
										this.classMembers.add(aMethod);
									}
								}else if(leftIdAsString.startsWith(this.getName()+".")){// not very good way of handling it A.foo= function(){....} foo is static method
									EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredWithinClassBody, MethodType.staticMethod);
									Method aMethod= new Method(leftIdAsString.replace(this.getName()+".", ""), this,right.asFunctionDeclaration(), kinds);
									this.classMembers.add(aMethod);
								}	
							}
						}
					}
				}
			}else if(abstractStatement instanceof CompositeStatement){
				if(!(abstractStatement.getStatement() instanceof FunctionDeclarationTree) &&  
						!(abstractStatement.getStatement() instanceof LabelledStatementTree)){ // we don't go inside functions or object literals
					identifyMethodsWithinClassBody(((CompositeStatement)abstractStatement).getStatements());		
				}
//				else if((abstractStatement.getStatement() instanceof LabelledStatementTree)){
//					LabelledStatementTree LabelledStatementTree=abstractStatement.getStatement().asLabelledStatement();
//					if(LabelledStatementTree.statement instanceof FunctionDeclarationTree){
//						System.out.println("================>" + LabelledStatementTree.location.start.line+ "   "+ LabelledStatementTree.location.start.source.name);
//						
//						this.methods.put(LabelledStatementTree.name.value,abstractStatement);
//					}
//				}
			}
		}
		
	}
	
	public void identifyMethodsAddedToClassPrototype(){
		
		// for all assignments in the module (but not in the current class-body) search for 
		// ClassName.prototype.FunctionName = FunctionDeclarationTree or
		// ClassName.prototype = ObjectLiteral containing function or
		// ClassName.FunctionName= FunctionDeclarationTree => here we have static method
		for( AbstractExpression abstractExpression :this.parentModule.getProgram().getAssignmentExpressionList()){
			if(!this.functionDeclaration.getAssignments().contains(abstractExpression)){ // we are interested in assignment out of class body
				if (abstractExpression.getExpression() instanceof BinaryOperatorTree) {
					BinaryOperatorTree binaryOperatorTree = abstractExpression.getExpression().asBinaryOperator();
					AbstractIdentifier leftId = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
					ParseTree right= binaryOperatorTree.right;
					if(right instanceof FunctionDeclarationTree){
						if (leftId instanceof CompositeIdentifier) {
							if (leftId.asCompositeIdentifier().toString().contains(this.functionDeclaration.getName()+".prototype.") ) {
								String methodName=leftId.asCompositeIdentifier().toString().replace(this.functionDeclaration.getName()+".prototype.", "");
								//this.allMethods.put(methodName, abstractExpression);
								EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredOutOfClassBody);
								Method aMethod= new Method(methodName,this,((FunctionDeclarationTree)right), kinds);
								this.classMembers.add(aMethod);
							}else if(leftId.asCompositeIdentifier().toString().contains(this.functionDeclaration.getName()+".")){ // not very good way of handling it A.foo= function(){....} foo is static method
								String methodName=leftId.asCompositeIdentifier().toString().replace(this.functionDeclaration.getName()+".", "");
								//this.allMethods.put(methodName, abstractExpression);
								EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredOutOfClassBody, MethodType.staticMethod);
								Method aMethod= new Method(methodName,this, ((FunctionDeclarationTree)right), kinds);
								this.classMembers.add(aMethod);
							}
						}else{
							// we don't care then
						}	
					}else if(right instanceof ObjectLiteralExpressionTree){ // If Car is a class => Car.prototype = { getInfo: function () { return this.make + ', ' + this.model };};
						for(ParseTree property: right.asObjectLiteralExpression().propertyNameAndValues){
							if(property.asPropertyNameAssignment().value instanceof FunctionDeclarationTree){
								//int size=property.asPropertyNameAssignment().value.asFunctionDeclaration().functionBody.asBlock().statements.size();
								Token methodName=property.asPropertyNameAssignment().name;
								//this.allMethods.put(methodName.toString(), abstractExpression);
								EnumSet<MethodType> kinds=  EnumSet.of(MethodType.declaredOutOfClassBody);
								Method aMethod= new Method(methodName.toString(),this, property.asPropertyNameAssignment().value.asFunctionDeclaration(), kinds);
								this.classMembers.add(aMethod);
							}
						}
					}	
				}
			}
		}
	}

	public void identifyInheritanceRelatedMethods() {
		
		if(this.subTypes.size()!=0){
			Map<Method , Set<Method>> candidates= new HashMap<Method , Set<Method>>();
			for(ClassMember memebr: this.classMembers){
				if(memebr instanceof Method){
					Method method=(Method) memebr;
					Set<Method> aSet= new HashSet<Method>();
					for(ClassDeclaration sub: this.subTypes){
						for(ClassMember subMemebr :sub.getClassMembers()){
							if(subMemebr instanceof Method){
								Method subMethod=(Method) subMemebr;
								if(method.getName().contentEquals(subMethod.getName())){
									aSet.add(subMethod);
									subMethod.getKinds().add(MethodType.overriding);
									break;
								}
							}
							
						}
					}
					if(aSet.size()>0){
						candidates.put(method, aSet);
						method.getKinds().add(MethodType.overriden);
					}
				}
				
			}
			for(Method methodInSuperClass: candidates.keySet()){
				if(candidates.get(methodInSuperClass).size()==this.subTypes.size()){// all subclasses have the same method 
					BlockTree body=methodInSuperClass.getParseTree().asFunctionDeclaration().functionBody.asBlock();
					if(body.statements.size()==0){
						methodInSuperClass.getKinds().add(MethodType.abstractMethod);
					}
					else if(body.statements.size()==1){ // if we have only one statement
						//System.out.println( body.statements.get(0).location.start.source.name+ ", "+ body.statements.get(0).location.start.line+1+", "+body.statements.get(0).getClass());
						if(body.statements.get(0) instanceof ThrowStatementTree){
							ThrowStatementTree throwStatement=body.statements.get(0).asThrowStatement();
							ParseTree value=throwStatement.value;
							methodInSuperClass.getKinds().add(MethodType.abstractMethod);
							
						}if(body.statements.get(0) instanceof ExpressionStatementTree){
							ExpressionStatementTree expressionStatementTree=body.statements.get(0).asExpressionStatement();
							if(expressionStatementTree.expression instanceof CallExpressionTree){
								CallExpressionTree call=expressionStatementTree.expression.asCallExpression();
								ParseTree operand=call.operand;
								AbstractIdentifier id=IdentifierHelper.getIdentifier(operand);
								String name=null;
								if(id instanceof CompositeIdentifier){
									name=id.getIdentifierName();
									
								}else if(id instanceof PlainIdentifier){
									name=id.getIdentifierName();
								}
								if(name !=null && (name.contentEquals("alert")|| name.contentEquals("console.log"))){
									methodInSuperClass.getKinds().add(MethodType.abstractMethod);
								}
							}
							
						}
					}
				}
			}
			
		}
	}
	
}
