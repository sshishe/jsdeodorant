package ca.concordia.jsdeodorant.analysis;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.CompositeStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationExpressionNature;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.InferenceType;
import ca.concordia.jsdeodorant.analysis.decomposition.ObjectLiteralExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.Statement;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class ClassInferenceEngineStricMode {
	public static void run(Module module) {
		for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
			if (functionDeclaration.isTypeDeclaration()){ 
				continue;
			}

			if (functionDeclaration instanceof FunctionDeclarationExpression) {
				FunctionDeclarationExpression functionDeclarationExpression = (FunctionDeclarationExpression) functionDeclaration;
				if (functionDeclarationExpression.getFunctionDeclarationExpressionNature() == FunctionDeclarationExpressionNature.IIFE){ 
					continue;
				}
			}
			// Here we check if the function is assigned to a "prototype" or "this"
			// of other object then we don't analyze the body
			boolean proceed=false;
			if(!(functionDeclaration.getQualifiedName().contains(".prototype.") ||
					functionDeclaration.getQualifiedName().contains("this."))){
				
				AbstractIdentifier id=functionDeclaration.getRawIdentifier();
				if(id !=null){
					String rawIdentifier=null;
					if(id instanceof PlainIdentifier){
						rawIdentifier=id.asPlainIdentifier().toString();
					}else if(id instanceof CompositeIdentifier){
						rawIdentifier=id.asCompositeIdentifier().toString();
					}
					if(rawIdentifier!=null && 
							!(rawIdentifier.contains(".prototype.") || rawIdentifier.contains("this.") )){
						proceed=true;
					}
					
				}else{
					proceed=true;
				}
				
				
			}

			
			if(proceed){
				int totalMethodsInsideTypeBody=assignedMethodsInsideTypeBody(module, functionDeclaration);
				int totalAttributesInsideTypeBody=assignedAttributesInsideTypeBody(module, functionDeclaration);
				int totalMethodsOutSideOutSideBody=assignedMethodToPrototypeOutSideBody(module, functionDeclaration);
				int totalAttributesOutSideOutSideBody=assignedAttributesToPrototypeOutSideBody(module, functionDeclaration);
				int totalObjectLiteralToPrototypeOutSideBody=assignObjectLiteralToPrototypeOutSideBody(module, functionDeclaration);
				
				SourceContainer parent = null;
				
				if( functionDeclaration instanceof FunctionDeclarationExpression){
					 parent=((FunctionDeclarationExpression) functionDeclaration).getParent();
				}else if(functionDeclaration instanceof FunctionDeclarationStatement){
					 parent=((FunctionDeclarationStatement) functionDeclaration).getParent();
				}
				
				FunctionDeclaration parentFunction=getParentFunction(parent);
				
				if(totalMethodsInsideTypeBody>0 || totalAttributesInsideTypeBody>0){
					createTypeDeclaration(module, functionDeclaration ,parentFunction,InferenceType.Constructor_Body_Analysis);
				}else if(totalMethodsOutSideOutSideBody>0){
					createTypeDeclaration(module, functionDeclaration ,parentFunction, InferenceType.Methods_Added_To_Prototype);
				}else if(totalObjectLiteralToPrototypeOutSideBody>0){
					createTypeDeclaration(module, functionDeclaration ,parentFunction, InferenceType.ObjectLiteral_Added_ToPrototype);
				}
				nowSetClassesToNotFoundByObjectCreations(module);
			}
		}
	}

	private static void createTypeDeclaration(Module module, FunctionDeclaration functionDeclaration,
		FunctionDeclaration parentFunction, InferenceType infType) {
		String parentName=null;
		String parentMiddleName=null; // when we have A=B=function(){...} then B is middle name
		if(parentFunction!=null)
			parentName=parentFunction.getName();
		if(parentFunction instanceof FunctionDeclarationExpression){
			parentMiddleName=((FunctionDeclarationExpression)parentFunction).getMiddleName();
		}
		if((parentName!=null && parentName.contentEquals(functionDeclaration.getName()))||
				(parentMiddleName!=null && parentMiddleName.contentEquals(functionDeclaration.getName()))){ // then the parentFunction is class and the current function is its constructor
			if(parentFunction !=null){
				TypeDeclaration aTypeDeclaration=createTypeDeclaration(module, parentFunction, infType);
				aTypeDeclaration.setHasConstrucotr(true);
				aTypeDeclaration.getConstructors().add(functionDeclaration);
				functionDeclaration.SetIsConstructor(true);
				// if we already identified the  functionDeclaration as class, then we should remove it form classList
				if(functionDeclaration.isTypeDeclaration()){
					functionDeclaration.setClassDeclaration(false);
					TypeDeclaration toRemove=null;
					for(TypeDeclaration aType: module.getTypes()){
						if(aType.getFunctionDeclaration().equals(functionDeclaration)){
							toRemove=aType;
							break;
						}
					}
					if(toRemove!=null){
						module.getTypes().remove(toRemove);
					}
				}
			}
		}else{
			createTypeDeclaration(module, functionDeclaration, infType);
		}
	}
	
	private static FunctionDeclaration getParentFunction(SourceContainer parent ){
		
		FunctionDeclaration parentFunction=null;
		FunctionDeclarationStatement parentFunctionDeclarationStatement=null;
		if(parent instanceof CompositeStatement){
			 if(((CompositeStatement)parent).getParentFunction() !=null){
				 parentFunction=((CompositeStatement)parent).getParentFunction();
				 return parentFunction;
			 }else{
				 return null;
			}
		 }else  if(parent instanceof FunctionDeclarationStatement){
			 parentFunctionDeclarationStatement=(FunctionDeclarationStatement) parent;
			 return parentFunctionDeclarationStatement;
		 }else{
			 return null;
		}
	}
	
	private static TypeDeclaration createTypeDeclaration(Module module, FunctionDeclaration functionDeclaration, InferenceType infType){
		
		AbstractIdentifier id=functionDeclaration.getRawIdentifier();
		if(id==null){
			id=functionDeclaration.getIdentifier();
		}
		TypeDeclaration typeDeclaration=null;
		String qualifiedName=null;
		if(id instanceof CompositeIdentifier){
			qualifiedName=id.asCompositeIdentifier().toString();
		}else if(id instanceof PlainIdentifier){
			qualifiedName=id.asPlainIdentifier().toString();
		}
		
		if(qualifiedName==null){
			typeDeclaration=module.createTypeDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, true, false);
			typeDeclaration.setInferenceType(infType);
		}
		else if(!qualifiedName.contains(".prototype.")){
			typeDeclaration=module.createTypeDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, true, false);
			typeDeclaration.setInferenceType(infType);
		}
		return typeDeclaration;
	}
	
	private static int assignedMethodsInsideTypeBody(Module module, FunctionDeclaration functionDeclaration) {

		int methodCounter=0;
		if(functionDeclaration.getStatements().size()>0){
			List<BinaryOperatorTree> assignmentsWithThisOnLeft=extractAssignemntsToThis( (CompositeStatement) functionDeclaration.getStatements().get(0));
			for(BinaryOperatorTree bot:assignmentsWithThisOnLeft){
				if(bot.right instanceof FunctionDeclarationTree){
					methodCounter++;
		
				}
			}
		}
		return methodCounter;
	}
	
	private static int assignedAttributesInsideTypeBody(Module module, FunctionDeclaration functionDeclaration) {

		int attrCounter=0;
		if(functionDeclaration.getStatements().size()>0){
			List<BinaryOperatorTree> assignmentsWithThisOnLeft=extractAssignemntsToThis( (CompositeStatement) functionDeclaration.getStatements().get(0));
			for(BinaryOperatorTree bot:assignmentsWithThisOnLeft){
				if(!(bot.right instanceof FunctionDeclarationTree)){
					attrCounter++; // I count everything !FunctionDeclarationTree  as attributes
				}
			}
		}
		return attrCounter;
	}
	
	private static int assignedMethodToPrototypeOutSideBody(Module module, FunctionDeclaration functionDeclaration) {
		
		List<AbstractExpression> assignments;// =module.getProgram().getAssignmentExpressionList();
		int methodCounter=0;
		SourceContainer parent;
		if(functionDeclaration instanceof FunctionDeclarationExpression){
			FunctionDeclarationExpression functionDeclarationExpression=(FunctionDeclarationExpression) functionDeclaration;
			parent=functionDeclarationExpression.getParent();
		}else{
			FunctionDeclarationStatement FunctionDeclarationStatement=(FunctionDeclarationStatement)functionDeclaration;
			parent=FunctionDeclarationStatement.getParent();
		}
		
		if(parent instanceof CompositeStatement){
			assignments=((CompositeStatement)parent).getAssignmentExpressionList();
		}else if (parent instanceof FunctionDeclarationExpression){
			assignments=((FunctionDeclarationExpression)parent).getAssignmentExpressionList();
		}else if (parent instanceof ObjectLiteralExpression){
			assignments=((ObjectLiteralExpression)parent).getAssignmentExpressionList();
		}else{
			assignments=((Program)parent).getAssignmentExpressionList();
		}
		
		
		for (AbstractExpression assignmentExpression : assignments) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				ParseTree left=binaryOperatorTree.left;
				ParseTree rigth=binaryOperatorTree.right;
				
				// I am only interested in assignments with function declaration on right
				if(rigth instanceof FunctionDeclarationTree){
					AbstractIdentifier leftId=IdentifierHelper.getIdentifier(left);
					if(leftId instanceof CompositeIdentifier){ // A.prototype.B= function
						if(((CompositeIdentifier)leftId).toString().contains(functionDeclaration.getName()+".prototype.")){
							// it is a naive way of checking, the better way may be is to 
							// check if assignmentExpression.getParent() and the functionDeclaration.getParent are the same 
							// because it could be that A.prototype.FunctionName could be for another class with same name A.
							methodCounter++;
						}
					}// we don't care if it is not CompositeIdentifier
				}
			}
		}		
		return methodCounter;
	}
	
	private static int assignedAttributesToPrototypeOutSideBody(Module module, FunctionDeclaration functionDeclaration) {
		
		List<AbstractExpression> assignments=module.getProgram().getAssignmentExpressionList();
		int attrCounter=0;
		for (AbstractExpression assignmentExpression : assignments) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				ParseTree left=binaryOperatorTree.left;
				ParseTree rigth=binaryOperatorTree.right;
				// I am  interested in assignments with no function declaration on right 
				// => this implies that every A.prototype.B= something where something is not function will be  counted as attribute
				if(!(rigth instanceof FunctionDeclarationTree)){
					AbstractIdentifier leftId=IdentifierHelper.getIdentifier(left);
					if(leftId instanceof CompositeIdentifier){ // A.prototype.B= function  
						if(((CompositeIdentifier)leftId).toString().contains(functionDeclaration.getName()+".prototype.")){
							
							attrCounter++;
						}
					}// we don't care if not CompositeIdentifier
				}
			}
		}		
		return attrCounter;
	}
	
	private static List<BinaryOperatorTree> extractAssignemntsToThis( CompositeStatement compositeStatement){
		List<BinaryOperatorTree> aList= new ArrayList<BinaryOperatorTree>();
		for (AbstractStatement statement : compositeStatement.getStatements()) {
			if(statement instanceof CompositeStatement){
				// LALEH: I don't want to go inside inner functions
				if(!(statement.getStatement() instanceof FunctionDeclarationTree)){
					aList.addAll(extractAssignemntsToThis((CompositeStatement) statement));
				}
			}else{
				if(statement.getStatement() instanceof ExpressionStatementTree){
					ExpressionStatementTree expressionStatement = statement.getStatement().asExpressionStatement();
					// LALEH: I am interested only on assignments
					if(expressionStatement.expression instanceof BinaryOperatorTree){
						BinaryOperatorTree binaryOperatorTree=expressionStatement.expression.asBinaryOperator();
						ParseTree left=binaryOperatorTree.left;
						if(left instanceof MemberExpressionTree){
							// LALEH: I am interested on assignment with "this." on left
							if(((MemberExpressionTree)left).operand instanceof ThisExpressionTree){
								aList.add(binaryOperatorTree);
							}
						}
					}
				}
			}
		}
		return aList;
	}
	
	private static int assignObjectLiteralToPrototypeOutSideBody(Module module, FunctionDeclaration functionDeclaration) {
		int counter=0;
		AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) functionDeclaration;
		SourceContainer parent = abstractFunctionFragment.getParent();
		List<AbstractExpression> assignmentList = null;
		if (parent instanceof Program) {
			Program program = (Program) parent;
			assignmentList = program.getAssignmentExpressionList();
		} else if (parent instanceof CompositeStatement) {
			CompositeStatement compositeParent = (CompositeStatement) parent;
			assignmentList = compositeParent.getAssignmentExpressionList();
		}
		if (assignmentList == null)
			return 0;

		for (AbstractExpression assignmentExpression : assignmentList) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				AbstractIdentifier left = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
				ParseTree right = binaryOperatorTree.right;
				if(right instanceof ObjectLiteralExpressionTree){ // LALEH: added the  check on right 
					if (left instanceof CompositeIdentifier) {
						if (functionDeclaration.getName().equals(left.asCompositeIdentifier().getLeftPart().toString()))
							if (((CompositeIdentifier) left).getMostRightPart().toString().contains("prototype")) {// bug here on src/selection/selection.js line 27, so I added the if to check the right hand side
								counter++;
							}
					}	
				}
			}
		}
		return counter;
	}
	
	private static void nowSetClassesToNotFoundByObjectCreations(Module module) {
		for (ObjectCreation objectCreation : module.getProgram().getObjectCreationList()) {
			if (objectCreation.getClassDeclaration() != null)
				for (TypeDeclaration typeDeclaration : module.getTypes()) {
					if (objectCreation.getIdentifier().equals(typeDeclaration.getName())) {
						objectCreation.setClassDeclaration(typeDeclaration, module);
						typeDeclaration.setMatchedAfterInference(true);
					}
				}
		}
	}

}
