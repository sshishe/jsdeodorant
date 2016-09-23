package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kohsuke.args4j.CmdLineParser;

import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class ClassDeclaration {
	private AbstractIdentifier identifier;
	private FunctionDeclaration functionDeclaration;
	private Map<String, AbstractExpression> methods;
	private Map<String, AbstractExpression> attributes;
	private boolean isInfered;
	private InferenceType inferenceType;
	private boolean hasNamespace;
	private int instantiationCount;
	private LibraryType libraryType;
	private boolean isAliased;
	// If method defines outside of the constructor, then keep LOC
	private int extraMethodLines;
	private Set<ClassDeclaration> superTypes;
	private Set<FunctionDeclaration> constructors;// transpiled code from typeScript has constructors
	private boolean hasConstructor;

	// After inferring the class, we try to resolve the corresponding 'new' which we were not able to matched before.
	private boolean matchedAfterInference;
	private Module parentModule;

	public ClassDeclaration(AbstractIdentifier identifier, FunctionDeclaration functionDeclaration, boolean isInfered, boolean hasNamespace, LibraryType libraryType, boolean isAliased, Module parentModule) {
		this.identifier = identifier;
		this.functionDeclaration = functionDeclaration;
		this.setParentModule(parentModule);
		this.attributes = new TreeMap<String, AbstractExpression>();
		this.methods = new TreeMap<String, AbstractExpression>();
		this.isInfered = isInfered;
		this.hasNamespace = hasNamespace;
		this.instantiationCount = 0;
		this.libraryType = libraryType;
		this.isAliased = isAliased;
		this.superTypes= new HashSet<ClassDeclaration>();
		this.constructors= new HashSet<FunctionDeclaration>();
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

	public Map<String, AbstractExpression> getMethods() {
		return methods;
	}

	public void addMethod(String name, AbstractExpression expression, int locToBeAdded) {
		this.extraMethodLines += locToBeAdded;
		this.methods.put(name, expression);
	}

	public void setMethods(Map<String, AbstractExpression> methods) {
		this.methods = methods;
	}

	public Map<String, AbstractExpression> getAttributes() {
		return attributes;
	}

	public void addAttribtue(String name, AbstractExpression expression) {
		this.attributes.put(name, expression);
	}

	public void setAttributes(Map<String, AbstractExpression> attributes) {
		this.attributes = attributes;
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

	public Set<ClassDeclaration> getSuperTypes() {
		return superTypes;
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
	
	public void addToSuperType(ClassDeclaration aClassDeclaration){
		if(this.superTypes.size()==0){
			this.superTypes.add(aClassDeclaration);
		}else{
			for(ClassDeclaration c: this.superTypes){
				if(!c.getFunctionDeclaration().equals(aClassDeclaration.getFunctionDeclaration())){
					this.superTypes.add(aClassDeclaration);
				}
			}
		}
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
							System.out.println("\t\t\t  right instanceof FunctionDeclarationTree");
						}else{ // we are looking for attribute thus right should not be FunctionDeclarationTree
							AbstractIdentifier leftId=IdentifierHelper.getIdentifier(left);
							if(leftId instanceof CompositeIdentifier){
								String leftIdAsString=leftId.asCompositeIdentifier().toString();
								if(leftIdAsString.startsWith("this.")){
									if(!(leftIdAsString.split(".").length>2)){ 
										// more strict rules only one dot => this.SOMETHING and NOT  this.SOMETHING.OTHERTHINGS
										//this.attributes.put(((CompositeIdentifier) leftId).getMostRightPart(), abstractStatement);
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
									// found method
								}	
							}
						}
					}
				}
			}else if(abstractStatement instanceof CompositeStatement){
				if(!(abstractStatement.getStatement() instanceof FunctionDeclarationTree) &&  
						!(abstractStatement.getStatement() instanceof LabelledStatementTree)){ // we don't go inside functions or object literals
					identifyMethodsWithinClassBody(((CompositeStatement)abstractStatement).getStatements());		
				}else if((abstractStatement.getStatement() instanceof LabelledStatementTree)){
					LabelledStatementTree LabelledStatementTree=abstractStatement.getStatement().asLabelledStatement();
					if(LabelledStatementTree.statement instanceof FunctionDeclarationTree){
						// found method
					}
				}
			}
		}
		
	}
	
	public void identifyMethodsAddedToClassPrototype(){
		if(this.parentModule.getSourceFile().getName().endsWith("/objectLiteralAsClass.js")){
			System.out.println("\t\t\t  on place");
		}
		
		for( AbstractExpression abstractExpression :this.parentModule.getProgram().getAssignmentExpressionList()){
			if(!this.functionDeclaration.getAssignments().contains(abstractExpression)){ // we are interested in assignment out of class body
				if (abstractExpression.getExpression() instanceof BinaryOperatorTree) {
					BinaryOperatorTree binaryOperatorTree = abstractExpression.getExpression().asBinaryOperator();
					AbstractIdentifier leftId = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
					ParseTree right= binaryOperatorTree.right;
					if(right instanceof FunctionDeclarationTree){
						if (leftId instanceof CompositeIdentifier) {
							if(leftId instanceof CompositeIdentifier){
								if (leftId.asCompositeIdentifier().toString().contains(this.functionDeclaration.getName()+".prototype.") ) {
									// found method
								}
							}
						}	
					}	
				}
			}
		}
		
		// Now identify functions that are added to the prototype in form of objectLiteral => is it valid?
		AbstractFunctionFragment aff=(AbstractFunctionFragment) this.functionDeclaration;
		for (ObjectLiteralExpression objectLiteralExpression:this.parentModule.getProgram().getObjectLiteralList()){
			if(!aff.getObjectLiteralExpressionList().contains(objectLiteralExpression)) { //we are interested in ObjectLiteral out of class body
				objectLiteralExpression.getExpression();
			}
		}
	}

	
}
