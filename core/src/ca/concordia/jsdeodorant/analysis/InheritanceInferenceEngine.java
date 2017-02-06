package ca.concordia.jsdeodorant.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.FunctionInvocation;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceElement;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.CompositeStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationExpressionNature;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionKind;
import ca.concordia.jsdeodorant.analysis.decomposition.InferenceType;
import ca.concordia.jsdeodorant.analysis.decomposition.Statement;
import ca.concordia.jsdeodorant.analysis.module.PackageSystem;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class InheritanceInferenceEngine {
	
	static Logger log  = Logger.getLogger(InheritanceInferenceEngine.class.getName());
	
	// this is for goog.inherits in ClosureLibrary + Helma + JavaScript way of inheritance (sub = Object.create(super) or sub.prototype = new super())
	//key: name of module
	// value: set of start+"#"+end+"#"ParentName+"#"+childName where "start" and "end" are the start-line and end-line of the statement from which the child is extracted
	private Map<Module, Set<String>>potentialInheritenceRelations;
	// this is for call to _extend(A,B) in angular
	// key: module
	// value: set of ClassDeclaration with  __extends(A, B); as its statement
	private Map<Module, Set<TypeDeclaration>> potentialSubTypes;
	
	private PackageSystem packageSystem;
	private String inheritenceAPIMethodName;
	private final String CALL = "call";
	private final String APPLY = "apply";
	
	//key: subclass
	// value: Name of potential superType (the body of subclass contains: Super.call(this,arg1, agr2,..) or super.apply(this,arg1, agr2,..))
	private Map<Module, Map<TypeDeclaration, String>> potentialSuperTypes;
	
	public InheritanceInferenceEngine(){
		potentialInheritenceRelations = new HashMap<Module, Set<String>>();
		potentialSubTypes = new HashMap<Module, Set<TypeDeclaration>>();
		potentialSuperTypes = new HashMap<Module, Map<TypeDeclaration, String>>();
	}
	
	public void configure(PackageSystem packageSystem) {
		this.packageSystem = packageSystem;
		if(packageSystem.equals(PackageSystem.ClosureLibrary)) {
			this.inheritenceAPIMethodName = "goog.inherits";
		}else if(packageSystem.equals(PackageSystem.Helma)) {
			this.inheritenceAPIMethodName = "jala.Form.extend";
		}
	}

	public void run(Module module) {
		
		log.debug("Identifying inheritence clues in: " + module.getSourceFile().getName());
		List<FunctionDeclaration> functionDeclarations = module.getProgram().getFunctionDeclarationList();
		for(FunctionDeclaration f: functionDeclarations){
			//this is for call-pattern see example test/inheritance/callPattern.js
			analyzeInvocationToApplyandCall(f,module);
			ExtractCallToExtend(f,module);
		}
		
		this.findPrototypeChainInitilization(module);
		
		//this is for call to goog.inherits(childCtor, parentCtor) (see example test/inheritance/goog.inherits.js) 
		// or call to jala.Form.extend(subClass, superClass) in Helma
		if(packageSystem.equals(PackageSystem.ClosureLibrary) || packageSystem.equals(PackageSystem.Helma)){
			List<FunctionInvocation> invocations = module.getProgram().getFunctionInvocationList();
			for(FunctionInvocation functionInvocation: invocations){		
				analyzecalltoInheritanceAPIFunction(functionInvocation,module, inheritenceAPIMethodName);
			}
		}
	}
	
	// call to __extend(subName, superName) in Angular, inside body of a function declaration
	private void ExtractCallToExtend(FunctionDeclaration f, Module module) {
		
		if(f.getStatements().size()>0 && f.getParameters().size()>0) {
			AbstractStatement body = f.getStatements().get(0); 
			ParseTree formalParam = f.getParameters().get(0).getExpression();
			if(formalParam instanceof IdentifierExpressionTree){
				String fromalParamName = formalParam.asIdentifierExpression().identifierToken.value;
				for(AbstractStatement abstractStatement: ((CompositeStatement)body).getStatements()){
					if(abstractStatement.getFunctionInvocationList().size()>0){
						boolean found = false;
						for(FunctionInvocation invocation: abstractStatement.getFunctionInvocationList()){
							if(invocation.getMemberName().contentEquals("__extends") || invocation.getMemberName().contentEquals("extend")){ 
								ParseTree arg0 = invocation.getArguments().get(0).getExpression();
								if(arg0 instanceof IdentifierExpressionTree){
									if(arg0.asIdentifierExpression().identifierToken.value.contentEquals(f.getName())){
										ParseTree arg1 = invocation.getArguments().get(1).getExpression();
										if(arg1 instanceof IdentifierExpressionTree){
											if(arg1.asIdentifierExpression().identifierToken.value.contentEquals(fromalParamName)){
												TypeDeclaration typeDeclaration;
												if(!f.isTypeDeclaration() && !f.isConstructor()){
													typeDeclaration = module.createTypeDeclaration(f.getRawIdentifier(), f, true, false);
													typeDeclaration.setInferenceType(InferenceType.Has_SuperType);
												}else{
													typeDeclaration = this.findTypeDeclarationInModule(f, module); // We know that current module should contains the class
												}
												if(typeDeclaration != null){
													this.adddToPotentialSubTypes(module, typeDeclaration);
												}
												found = true;
												break;
											}
										}
									}
								}
							}
						}
						if(found){
							break;
						}
						
					}
				}
			
			}
		}
		
		// iterate over the potentialSubTypes.get(module), see if you can find constructor definition in classDeclaration
		if(potentialSubTypes.containsKey(module)){
			for(TypeDeclaration aType: potentialSubTypes.get(module)){
				FunctionDeclaration fn = aType.getFunctionDeclaration();
				AbstractStatement body = fn.getStatements().get(0); 
				for(AbstractStatement abstractStatement: ((CompositeStatement)body).getStatements()){
					for(FunctionDeclaration aFunctionDeclaration: abstractStatement.getFunctionDeclarationList()){
						if(aFunctionDeclaration.getName().contentEquals(fn.getName())){
							aFunctionDeclaration.SetIsConstructor(true);
							aType.getConstructors().add(aFunctionDeclaration);
							aType.setHasConstrucotr(true);
						}
					}
				}
			}	
		}
	}

	private void adddToPotentialSubTypes(Module module, TypeDeclaration aType) {
		if(this.potentialSubTypes.containsKey(module)){
			potentialSubTypes.get(module).add(aType);
		}else{
			Set<TypeDeclaration> aSet = new HashSet<TypeDeclaration>();
			aSet.add(aType);
			this.potentialSubTypes.put(module, aSet);
		}
		
	}

	private void processPotentialSubTypes(){
		Map<TypeDeclaration ,String> subTypeSuperTypeMap = new HashMap<TypeDeclaration ,String>(); // the value is of type IdentifierExpressionTree or MemberExpressionTree
		for(Module module: this.potentialSubTypes.keySet()){	
			for(TypeDeclaration aTypeDeclaration: this.potentialSubTypes.get(module)){
				boolean proceed = true;
				if (aTypeDeclaration.getFunctionDeclaration() instanceof FunctionDeclarationExpression) {
					FunctionDeclarationExpression functionDeclarationExpression  = (FunctionDeclarationExpression) aTypeDeclaration.getFunctionDeclaration();
					if (functionDeclarationExpression.getFunctionDeclarationExpressionNature()  ==  FunctionDeclarationExpressionNature.IIFE){
						if(functionDeclarationExpression.getIIFEParam() != null){
							subTypeSuperTypeMap.put(aTypeDeclaration,functionDeclarationExpression.getIIFEParam());
						}
						proceed = false;
					}
						
				}
				if(proceed){
					List<SourceElement>  elements = module.getProgram().getSourceElements();
					for (SourceElement sourceElement : elements) {
						if(sourceElement instanceof Statement){
							ParseTree statemenParseTree = ((Statement)sourceElement).getStatement();
							statemenParseTree.getClass();
							if (statemenParseTree instanceof VariableStatementTree){
								if(((VariableStatementTree)statemenParseTree).declarations.declarations.size()>0){
									VariableDeclarationTree var = ((VariableStatementTree)statemenParseTree).declarations.declarations.get(0);
									IdentifierExpressionTree id = (IdentifierExpressionTree) var.lvalue;
									String name = id.identifierToken.value;
									if(name.contentEquals(aTypeDeclaration.getName())){
										ParseTree initializer = var.initializer;
										if(initializer instanceof CallExpressionTree){
											if(initializer.asCallExpression().arguments.arguments.size()>0){
												ParseTree arg0 = initializer.asCallExpression().arguments.arguments.get(0);
												if(arg0 instanceof IdentifierExpressionTree){
													//String superTypeNme = arg0.asIdentifierExpression().identifierToken.value;
													String candidateSuperTypeName;
													AbstractIdentifier abstractIdentifier = IdentifierHelper.getIdentifier(arg0);
													String potentialSuperName  = null; // specific to angular 
													if(abstractIdentifier instanceof PlainIdentifier){
														potentialSuperName = abstractIdentifier.asPlainIdentifier().toString();
													}else{
														potentialSuperName = abstractIdentifier.asCompositeIdentifier().toString();
													}
													
													subTypeSuperTypeMap.put(aTypeDeclaration,potentialSuperName);
													break;
												}else if(arg0 instanceof MemberExpressionTree){
													String candidateSuperTypeName;
													AbstractIdentifier abstractIdentifier = IdentifierHelper.getIdentifier(arg0);
													String potentialSuperName  = null; // specific to angular 
													if(abstractIdentifier instanceof PlainIdentifier){
														potentialSuperName = abstractIdentifier.asPlainIdentifier().toString();
													}else{
														potentialSuperName = abstractIdentifier.asCompositeIdentifier().toString();
													}
													subTypeSuperTypeMap.put(aTypeDeclaration,potentialSuperName);
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		Map<TypeDeclaration ,Set<FunctionDeclaration>> potentialSubSuperMap = new HashMap<TypeDeclaration ,Set<FunctionDeclaration>>();
		for(TypeDeclaration aTypeDeclaration:subTypeSuperTypeMap.keySet()){
			String superTypeCandiate = subTypeSuperTypeMap.get(aTypeDeclaration);
			String candidateSuperTypeName;
			String potentialDependencyName  = null; // specific to angular 
			if(superTypeCandiate.contains(".")){
				potentialDependencyName = superTypeCandiate.split("\\.")[0];
				int potentialDependencyNameLength = potentialDependencyName.length();
				candidateSuperTypeName = superTypeCandiate.substring(potentialDependencyNameLength+1);
				
			}else{
				candidateSuperTypeName = superTypeCandiate;
			}
			
//			System.out.println("subName: "+ aclassDeclaration.getName()+ " in file: "+ aclassDeclaration.getParentModule().getSourceFile().getName());
//			System.out.println("superName : "+ candidateSuperTypeName);
			// 1- first look in the module where the class is defined
			Module module = aTypeDeclaration.getParentModule();
			boolean found = false;
			for(FunctionDeclaration f: module.getProgram().getFunctionDeclarationList()){
				if(f.getName().contentEquals(candidateSuperTypeName)){
					if(potentialSubSuperMap.containsKey(aTypeDeclaration)){
						potentialSubSuperMap.get(aTypeDeclaration).add(f);
					}else{
						Set<FunctionDeclaration> aSet = new HashSet<FunctionDeclaration>();
						aSet.add(f);
						potentialSubSuperMap.put(aTypeDeclaration, aSet);
					}
					found = true;
				}
			}
			// 2- if not found search in modules that are imported
			if(!found && potentialDependencyName!= null){	
				for(Dependency d: 	module.getDependencies()){
					if(d.getName().contentEquals(potentialDependencyName)){
						Module importedModule = d.getDependency();
						for(FunctionDeclaration fn: importedModule.getProgram().getFunctionDeclarationList()){
							if(fn.getName().contentEquals(candidateSuperTypeName)){
								if(potentialSubSuperMap.containsKey(aTypeDeclaration)){
									potentialSubSuperMap.get(aTypeDeclaration).add(fn);
								}else{
									Set<FunctionDeclaration> aSet = new HashSet<FunctionDeclaration>();
									aSet.add(fn);
									potentialSubSuperMap.put(aTypeDeclaration, aSet);
								}
							}
						}
					}
				}	
			}
		}
		
		// why the value of potentialSubSuperMap is a set? because some are constructors of others
		for(TypeDeclaration subType: potentialSubSuperMap.keySet()){
			Map<FunctionDeclaration, FunctionDeclaration> superClassConstructorMap = this.removeConstrucotrsIn((potentialSubSuperMap.get(subType)));
			for(FunctionDeclaration parent: superClassConstructorMap.keySet()){
				Module parentModule = this.findModuleForFunctionDeclaration(parent);
				TypeDeclaration superType = parentModule.createTypeDeclaration(parent.getIdentifier(), parent, true, false);
				superType.setInferenceType(InferenceType.Has_SubType);
				if(superClassConstructorMap.get(parent)!= null){
					superType.setHasConstrucotr(true);
					superType.getConstructors().add(superClassConstructorMap.get(parent));
					superClassConstructorMap.get(parent).SetIsConstructor(true);
				}
				subType.setSuperType(superType);
				superType.addToSubTypes(subType);
			}
		}
		
	}
	
	
	public void buildInheritenceRelation(PackageSystem packageSystem){
		log.debug("START Analyzing Inheritance");
		if((packageSystem.equals(PackageSystem.ClosureLibrary) ||  packageSystem.equals(PackageSystem.Helma))
				&& this.potentialInheritenceRelations.size()>0){
			this.processPotentialInheritenceRelations();
		}else if(packageSystem.equals(PackageSystem.CommonJS) ){
			this.processPotentialSubTypes();
		}
		
		this.processPotentialSuperTypes();
		log.debug("DONE Analyzing Inheritance");
	}
	

	private void processPotentialSuperTypes() {
	
		for(Module aModule: this.potentialSuperTypes.keySet()){
			for(TypeDeclaration aSubType: this.potentialSuperTypes.get(aModule).keySet()){
				String potentialSuperTypeName = this.potentialSuperTypes.get(aModule).get(aSubType);
				FunctionDeclaration candiadte = null;
				boolean found = false;
				for(FunctionDeclaration afn:aModule.getProgram().getFunctionDeclarationList()){
					if(afn.getName().contentEquals(potentialSuperTypeName)){
						found = true;
						candiadte = afn;
						if(!afn.isTypeDeclaration()){
							TypeDeclaration superType  = aModule.createTypeDeclaration(candiadte.getIdentifier(), candiadte, true, false);
							superType.setInferenceType(InferenceType.Has_SubType);
							aSubType.setSuperType(superType);
							superType.addToSubTypes(aSubType);
						}else{
							TypeDeclaration superType  = findTypeDeclarationInModule(afn, aModule);
							aSubType.setSuperType(superType);
							superType.addToSubTypes(aSubType);
						}
						break;
					}
				}
				if(!found){
					for(Dependency d: aModule.getDependencies()){
						Module importedModule = d.getDependency();
						for(FunctionDeclaration fn: importedModule.getProgram().getFunctionDeclarationList()){
							if(fn.getName().contentEquals(potentialSuperTypeName)){
								candiadte = fn;
								found = true;
								if(!fn.isTypeDeclaration()){
									TypeDeclaration superType = importedModule.createTypeDeclaration(candiadte.getIdentifier(), candiadte, true, false);
									superType.setInferenceType(InferenceType.Has_SubType);
									aSubType.setSuperType(superType);
									superType.addToSubTypes(aSubType);
								}else{
									TypeDeclaration superType = findTypeDeclarationInModule(fn, importedModule);
									aSubType.setSuperType(superType);
									superType.addToSubTypes(aSubType);
								}
								break;
							}
						}
						if(found){
							break;
						}
					}
				}
			}
		}
	}

	
	private void processPotentialInheritenceRelations(){
		for(Module module: this.potentialInheritenceRelations.keySet()){
			for(String detail: this.potentialInheritenceRelations.get(module)){
				String[] parts = detail.split("#");
				
				int startLine = Integer.valueOf(parts[0]);
				int endLine = Integer.valueOf(parts[1]);
				String parentName = parts[2];
				String childName = parts[3];
				Set<FunctionDeclaration> potentialParents = new HashSet<FunctionDeclaration>();
				Set<FunctionDeclaration> potentialChilds = new HashSet<FunctionDeclaration>();
				for(FunctionDeclaration fn: module.getProgram().getFunctionDeclarationList()){
					if(fn.getName().contentEquals(parentName)){
						potentialParents.add(fn);
					}else if(fn.getName().contentEquals(childName)){
						potentialChilds.add(fn);
					}
				}
				TypeDeclaration parent = findTypeDeclaration(module, startLine, endLine, parentName, potentialParents);
				TypeDeclaration child = findTypeDeclaration(module, startLine, endLine, childName, potentialChilds);
				
				if(parent!= null){
					parent.setInferenceType(InferenceType.Has_SubType);
				}if(child!= null){
					child.setInferenceType(InferenceType.Has_SuperType);
				}
				if(parent!= null &&child!= null){
					parent.addToSubTypes(child);
					child.setSuperType(parent);
				}
				
			}
		}
	}

	private TypeDeclaration findTypeDeclaration(Module module, int startLine, int endLine, String functionName,
			Set<FunctionDeclaration> candidates) {
		FunctionDeclaration candidate = null;
		FunctionDeclaration constructorOfCandidate = null;
		module.getSourceFile().getName();
	
		if(candidates.size() == 1){
			candidate = candidates.iterator().next();
		}else if(candidates.size()>1){
			// first remove constructor 
			 Map<FunctionDeclaration, FunctionDeclaration> parentClassConstructorMap = this.removeConstrucotrsIn(candidates);
			 if(parentClassConstructorMap.keySet().size() == 1){
				 candidate = parentClassConstructorMap.keySet().iterator().next();
				 constructorOfCandidate = parentClassConstructorMap.get(candidate);
			 }else{ // find the most close one
				 candidate = findTheMostClosetFunctionDeclaration(parentClassConstructorMap.keySet(), startLine, endLine);
				 constructorOfCandidate = parentClassConstructorMap.get(candidate);
			 }
		}else if(candidates.size() == 0){
		   // search in the dependencies
			for(Dependency d: 	module.getDependencies()){
				Module importedModule = d.getDependency();
				for(FunctionDeclaration fn: importedModule.getProgram().getFunctionDeclarationList()){
					if(fn.getName().contentEquals(functionName)){
						candidates.add(fn); // it could be we have more than one functions with same name but no way to know which one is the one we are looking for
					}
				}
			}
		   
			if(candidates.size() == 1){
				candidate = candidates.iterator().next();
			}else if(candidates.size()>1){ // if more than one is found then remove the constructor
				 Map<FunctionDeclaration, FunctionDeclaration> parentClassConstructorMap = this.removeConstrucotrsIn(candidates);
				 if(parentClassConstructorMap.keySet().size() == 1){
					 candidate = parentClassConstructorMap.keySet().iterator().next();
					 constructorOfCandidate = parentClassConstructorMap.get(candidate);
				 }else{
					// if more than one left then it is difficult to find
				 }
			}	
		}
		TypeDeclaration aType = null;
		if(candidate!= null){
			if(candidate.isTypeDeclaration()){
				aType = this.findTypeDeclarationInModule(candidate, findModuleForFunctionDeclaration(candidate));
			}else{
				Module aModule = findModuleForFunctionDeclaration(candidate);
				aType = aModule.createTypeDeclaration(candidate.getRawIdentifier(), candidate, true, false);
			}
			if(aType != null){
				if(constructorOfCandidate!= null){
					aType.setHasConstrucotr(true);
					aType.getConstructors().add(constructorOfCandidate);
				}
			}
		}
		
		return aType;
		
	}
	
	private FunctionDeclaration findTheMostClosetFunctionDeclaration(Set<FunctionDeclaration> targets, int startLine, int endLine) {
		FunctionDeclaration candidate = null;
		int min = 1000000000;
		for (FunctionDeclaration functionDeclaration : targets) {
			int currentStartLine = functionDeclaration.getFunctionDeclarationTree().location.start.line;
			int currentEndLine = functionDeclaration.getFunctionDeclarationTree().location.end.line;
			int diff = 0;
			if(currentStartLine <= startLine &&  currentEndLine <= startLine){ // defined before 
				diff = startLine-currentEndLine;
			}else if(currentStartLine > endLine){ //defined after 
				diff = currentStartLine-endLine;
			}else if(currentStartLine >= startLine &&  currentEndLine >= endLine){  // defined in the middle
				diff = 0;
			}
			if(diff< min){
				candidate = functionDeclaration;
			}else if(diff ==  min){
				System.out.println("Have no idea how to handle it");
				System.exit(0);
			}
		}
		return candidate;
	}
	
	// iterate over the give functions and find those that are constructor of others and
	// build a map of class->constructor
	// example var Foo = Function{ function Foo(){...}} the inner Foo is constructor of the outer one
	private Map<FunctionDeclaration, FunctionDeclaration> removeConstrucotrsIn(Set<FunctionDeclaration> aSet) {
		Map<FunctionDeclaration, FunctionDeclaration> classConstructorMap = new HashMap<FunctionDeclaration, FunctionDeclaration>();
		if(aSet.size()>1){
			Iterator<FunctionDeclaration> it = aSet.iterator();
			while(it.hasNext()){
				FunctionDeclaration current = it.next();
				AbstractStatement body = current.getStatements().get(0);
				boolean found = false;
				for(AbstractStatement abstractStatement: ((CompositeStatement)body).getStatements()){
					for(FunctionDeclaration aFunctionDeclaration: abstractStatement.getFunctionDeclarationList()){
						if(aFunctionDeclaration.getName().contentEquals(current.getName())){
							if (aSet.contains(aFunctionDeclaration)){
								classConstructorMap.put(current,aFunctionDeclaration);
								found = true;
							}
						}
					}
				}if(!found){
					classConstructorMap.put(current, null);
				}
			}
		}else if(aSet.size() == 1){
			classConstructorMap.put(aSet.iterator().next(), null);
		}
		return classConstructorMap;
	}
	

	private TypeDeclaration findTypeDeclarationInModule(FunctionDeclaration aFunctionDeclaration, Module module) {
	
		TypeDeclaration target = null;
		for(TypeDeclaration aType: module.getTypes()){
			if(aType.getFunctionDeclaration().equals(aFunctionDeclaration)){
				target = aType;
				break;
			}
		}
		return target;
	}
	
	private Module findModuleForFunctionDeclaration(FunctionDeclaration aFunctionDeclaration){
		String FileName = aFunctionDeclaration.getFunctionDeclarationTree().location.start.source.name;
		Module aModule = null;
		for (Module module : JSproject.getInstance().getModules()) {
			if(module.getSourceFile().getName().contentEquals(FileName)){
				aModule = module;
			}
		}
		return aModule;
	}

	private void analyzeInvocationToApplyandCall (FunctionDeclaration childCandidate, Module module){
		
		if (childCandidate.getStatements().size()>0){
			CompositeStatement functionBody = (CompositeStatement) childCandidate.getStatements().get(0);
			//A.B.C.call(this, arg1,arg2,..) or A.call(this, arg1,arg2,..), if it is the first statement
			// or A.B.apply(this,..) or A.apply(this, arg1,arg2,..)  here A.B.C.call/A.call is the superclass
			// we find A.B.C.call or A.call or or A.apply or A.B.apply, or null
			FunctionInvocation targetFunctionInvocation  = this.extractCallOrApplyInvocation(functionBody);
			AbstractIdentifier rawId = childCandidate.getRawIdentifier();
			String childCandidateName = null;
			boolean childCandidateNameStartsWithUpperCase=false;
			if(rawId instanceof PlainIdentifier){
				childCandidateName  = rawId.asPlainIdentifier().toString();
				childCandidateNameStartsWithUpperCase=Character.isUpperCase(childCandidateName.charAt(0));
			}else if(rawId instanceof CompositeIdentifier){
				childCandidateName = rawId.asCompositeIdentifier().toString();
				childCandidateNameStartsWithUpperCase=Character.isUpperCase((rawId.asCompositeIdentifier().getMostRightPart()).asPlainIdentifier().toString().charAt(0));
			}
			if (targetFunctionInvocation != null && childCandidateName!= null && !childCandidateName.contains(".prototype.")
					&& childCandidateNameStartsWithUpperCase){
				
//				System.out.println("in file: "+ module.getSourceFile().getName());
//				System.out.println("\t childCandidateName: "+childCandidateName);
				
				// extract the name
				AbstractIdentifier identifier = targetFunctionInvocation.getIdentifier();
				String invocationAsString = null;
				if(identifier instanceof CompositeIdentifier){
					CompositeIdentifier compositeIdentifier = identifier.asCompositeIdentifier();
					invocationAsString = compositeIdentifier.toString();
				}else {
					PlainIdentifier plainIdentifier = identifier.asPlainIdentifier();
					invocationAsString = plainIdentifier.toString();
				}
				String potentialSuperTypeName = null;
				if(invocationAsString.endsWith(this.CALL)){
					potentialSuperTypeName = invocationAsString.replace(this.CALL, "");
				}else if(invocationAsString.endsWith(this.APPLY)){
					potentialSuperTypeName = invocationAsString.replace(this.APPLY, "");
				}
				TypeDeclaration aTypeDeclaration = null;			
				if(!childCandidate.isTypeDeclaration()){
					FunctionDeclaration parentFunction = null;
					SourceContainer parent  = null;
					if( childCandidate instanceof FunctionDeclarationExpression){
						 parent = ((FunctionDeclarationExpression) childCandidate).getParent();
					}else if(childCandidate instanceof FunctionDeclarationStatement){
						 parent = ((FunctionDeclarationStatement) childCandidate).getParent();
					}
					
					if(parent instanceof CompositeStatement){
						 if(((CompositeStatement)parent).getParentFunction() != null){
							 parentFunction = ((CompositeStatement)parent).getParentFunction();
						 }
					 }else  if(parent instanceof FunctionDeclarationStatement){
						 parentFunction = (FunctionDeclarationStatement) parent;
					 }
					
					if(parentFunction!= null){
						if(parentFunction.getName().contentEquals(childCandidate.getName())){ // then the parent is class and childCandidate is its constructor
							 aTypeDeclaration = module.createTypeDeclaration(parentFunction.getIdentifier(), parentFunction, true, false);
							 aTypeDeclaration.setInferenceType(InferenceType.Has_SuperType);
							 aTypeDeclaration.setHasConstrucotr(true);
							 aTypeDeclaration.getConstructors().add(childCandidate);
							 childCandidate.SetIsConstructor(true);
						 }else{
							 aTypeDeclaration = module.createTypeDeclaration(childCandidate.getIdentifier(), childCandidate, true, false);
							 aTypeDeclaration.setInferenceType(InferenceType.Has_SuperType); 
						 }
					 }else{
						aTypeDeclaration = module.createTypeDeclaration(childCandidate.getIdentifier(), childCandidate, true, false);
						aTypeDeclaration.setInferenceType(InferenceType.Has_SuperType); 
					 }
				}else{
					//childCandidate is a class
					aTypeDeclaration = findTypeDeclarationInModule(childCandidate, module);
				}
				if(!aTypeDeclaration.hasConstructor()){// check to see if it has a constructor
					AbstractStatement body = aTypeDeclaration.getFunctionDeclaration().getStatements().get(0);
					for(AbstractStatement abstractStatement: ((CompositeStatement)body).getStatements()){
						for(FunctionDeclaration aFunctionDeclaration: abstractStatement.getFunctionDeclarationList()){
							if(aFunctionDeclaration.getName().contentEquals(aTypeDeclaration.getName())){
								aFunctionDeclaration.SetIsConstructor(true);
								aTypeDeclaration.getConstructors().add(aFunctionDeclaration);
								aTypeDeclaration.setHasConstrucotr(true);
							}
						}
					}
				}
				if(potentialSuperTypeName!= null ){
					if(this.potentialSuperTypes.containsKey(module)){
						this.potentialSuperTypes.get(module).put(aTypeDeclaration, potentialSuperTypeName);
					}else{
						 Map<TypeDeclaration, String> aMap = new  HashMap<TypeDeclaration, String>();
						this.potentialSuperTypes.put(module, aMap);
					}
				}
			}
		}
	}
	
	// The method searches for invocation like A.call(this, arg1,arg2,..)
	// A can  be composite:  A.B.C.call(this, arg1,arg2,..)
	// The method returns the whole  invocation
	// the method should not go inside innerFunctions
	private FunctionInvocation extractCallOrApplyInvocation(CompositeStatement compositeStatement){
		FunctionInvocation targetFunctionInvocation  = null;
		List<AbstractStatement> absStataments = compositeStatement.getStatements();
		//we only check the first statement
		if(absStataments.size()>0 ){
			AbstractStatement absStatement=absStataments.get(0);
			if(absStatement instanceof Statement && absStatement.getFunctionDeclarationExpressionList().size() == 0){
				targetFunctionInvocation  = this.extractCallInvocation(absStatement.getFunctionInvocationList());					
			}
		}
		
//		before we checked for call and apply anywhere withing the body
//		for(AbstractStatement absStatement: absStataments){
//			if(absStatement instanceof Statement && absStatement.getFunctionDeclarationExpressionList().size() == 0){ // we don't go to inner functions
//				//System.out.println(loc+ "   statement: " + absStatement.toString()+ "   type: "+absStatement.getClass());
//				targetFunctionInvocation  = this.extractCallInvocation(absStatement.getFunctionInvocationList());
//				if(targetFunctionInvocation!=  null){
//					return targetFunctionInvocation;
//				}
//			}else if(absStatement instanceof CompositeStatement){// it is a composite
//				if(! (absStatement instanceof FunctionDeclarationStatement)){
//					targetFunctionInvocation  = this.extractCallInvocation(((CompositeStatement)absStatement).getFunctionInvocationList());
//					if(targetFunctionInvocation!=  null){
//						return targetFunctionInvocation;
//					}
//				//	System.out.println(loc+ "   statement type: " +absStatement.getClass());
//					extractCallOrApplyInvocation(((CompositeStatement) absStatement));
//				}	
//			}
//		}
		return targetFunctionInvocation;
	}
	
	private FunctionInvocation extractCallInvocation(List<FunctionInvocation> invocations){
		FunctionInvocation targetInvocation  = null;
		for (FunctionInvocation invocation : invocations) {
			//System.out.println("\t identifier: "+ invocation.getIdentifier().toString());
			if(invocation.getIdentifier().toString().endsWith(this.CALL)|| invocation.getIdentifier().toString().endsWith(this.APPLY)){
				List<AbstractExpression> arguments = invocation.getArguments();
				if(arguments.size()>0 && arguments.get(0).toString().contentEquals("this")){
					//System.out.println("\t\t  arguments name: this" );
					targetInvocation = invocation;
				}
			}if(targetInvocation!= null){
				return targetInvocation;
			}
		}
		return targetInvocation;
	}
	
	// The method searches in the module body to find either of the patterns:
	// Child.prototype = new Parent() or 
	// Child.prototype  = Object.create(Parent.prototype);
	private boolean prototypeChainInitilizationExist(FunctionDeclaration childCandidate, String parentCandidateName,Module module){
		String ChildName = childCandidate.getName();
		//System.out.println("seraching for: "+ ChildName +".prototype in moudle:"+  module.getSourceFile().getName());
		List<AbstractExpression> assignments = module.getProgram().getAssignmentExpressionList();
		for (AbstractExpression assignment : assignments) {
			ParseTree tree = assignment.getExpression();
			BinaryOperatorTree binaryOperatorTree = ((BinaryOperatorTree)tree);
			int loc = assignment.getExpression().location.start.line+1;
			//System.out.println("\t found assignment on line: "+ loc);
			ParseTree left = binaryOperatorTree.left;
			if(IdentifierHelper.getIdentifier(left) instanceof CompositeIdentifier){
				String leftId = IdentifierHelper.getIdentifier(left).toString();
				if(leftId.contentEquals(ChildName+".prototype")){
					//System.out.println("\t found a match for: "+ ChildName +".prototype, at LHS on line: "+ loc +", will check RHS for Object.create");
					ParseTree right = binaryOperatorTree.right;
					String rightId = IdentifierHelper.getIdentifier(right).toString();
					if(right instanceof CallExpressionTree){
						if(rightId.contentEquals("Object.create")){
							//System.out.println("\t\t found a match for Object.create(..) on RHS, will check the argument ");
							ImmutableList<ParseTree> args = ((CallExpressionTree)right).arguments.arguments;
							//System.out.println("arg[0]: "+ IdentifierHelper.getIdentifierAsString(args.get(0)));
							//System.out.println("callee: "+ callee.getIdentifier().toString().replace(".call", ""));
							if(IdentifierHelper.getIdentifier(args.get(0)).toString().contentEquals(parentCandidateName+".prototype")){
								//System.out.println("\t\t\t found  a match in first arg");
								return true;
							}
						}
					}else if(right instanceof NewExpressionTree){
						//System.out.println("\t\t found a match for new on RHS, will check the function name");	
						if(rightId.contentEquals(parentCandidateName)){
							//System.out.println("\t\t\t found  a match for new "+ parentCandidateName +"(..)");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	// will populate the potentialInheritenceRelations with patterns like:
	// SubClass.prototype  = Object.create(SuperClass.prototype); or
	// SubClass.prototype = new SuperClass() or
	// SubClass.prototype = new SuperClass
	private void findPrototypeChainInitilization(Module module){
		List<AbstractExpression> assignments = module.getProgram().getAssignmentExpressionList();
		for (AbstractExpression assignment : assignments) {
			ParseTree tree = assignment.getExpression();
			BinaryOperatorTree binaryOperatorTree = ((BinaryOperatorTree)tree);
			ParseTree left = binaryOperatorTree.left;
			if(IdentifierHelper.getIdentifier(left) instanceof CompositeIdentifier){ // it should be composite we need  SubClass.prototype in LHS
				String leftId = IdentifierHelper.getIdentifier(left).toString();
				String potentialParentName = null;
				if(leftId.endsWith(".prototype")){
					//System.out.println("\t found a match for: "+ ChildName +".prototype, at LHS on line: "+ loc +", will check RHS for Object.create");
					ParseTree right = binaryOperatorTree.right;
					if(right instanceof NewExpressionTree){
						potentialParentName = IdentifierHelper.getIdentifier(right).toString();
					}else if(right instanceof CallExpressionTree){
						if(IdentifierHelper.getIdentifier(right).toString().contentEquals("Object.create")){
							ImmutableList<ParseTree> args = ((CallExpressionTree)right).arguments.arguments;
							String agr0 = IdentifierHelper.getIdentifier(args.get(0)).toString();
							if(agr0.endsWith(".prototype")){
								potentialParentName = agr0.replace(".prototype", "");
							}
						}
					}
				}
				if(potentialParentName!= null){
					String potentialChildName = leftId.replace(".prototype", "");
					int start = left.location.start.line;
					int end = left.location.end.line;
					
					if(this.potentialInheritenceRelations.keySet().contains(module)){
						this.potentialInheritenceRelations.get(module).add(start+"#"+end+"#"+potentialParentName+"#"+potentialChildName);
					}else{
						Set<String>  aSet = new HashSet<String>();
						aSet.add(start+"#"+end+"#"+potentialParentName+"#"+potentialChildName);
						this.potentialInheritenceRelations.put(module, aSet);
					}
				}
			}
		}
	}
	
	private void analyzecalltoInheritanceAPIFunction(FunctionInvocation functionInvocation, Module module, String taregtMethdName) {
		AbstractIdentifier functionIdentifier = functionInvocation.getIdentifier();
		String name;
		if(functionIdentifier instanceof CompositeIdentifier){
			CompositeIdentifier compositName = functionIdentifier.asCompositeIdentifier();
			name = compositName.toString();
		}else{
			PlainIdentifier plianName = functionIdentifier.asPlainIdentifier();
			name = plianName.toString();
		}
		
		if(name.contentEquals(taregtMethdName)){
			int start = functionInvocation.getCallExpressionTree().location.start.line;
			int end = functionInvocation.getCallExpressionTree().location.end.line;
			
			String child = null;
			String parent = null;
			
			AbstractExpression firstParam = functionInvocation.getArguments().get(0);
			ParseTree firstParamParseTree = firstParam.getExpression();

			if(firstParamParseTree instanceof MemberExpressionTree){
				child = this.ExtractName(firstParamParseTree.asMemberExpression());
			} else if(firstParamParseTree instanceof IdentifierExpressionTree){
				child = this.ExtractName(firstParamParseTree.asIdentifierExpression());
			}
			
			AbstractExpression secondParam = functionInvocation.getArguments().get(1);
			ParseTree secondParamParseTree = secondParam.getExpression();
			
			if(secondParamParseTree instanceof MemberExpressionTree){
				parent = this.ExtractName(secondParamParseTree.asMemberExpression());
			}else if(secondParamParseTree instanceof IdentifierExpressionTree){
				parent = this.ExtractName(secondParamParseTree.asIdentifierExpression());
			}
			
			if(child!=  null || parent != null){
				if (child  ==  null){
					child = "NOTFOUND";
				}else if (parent  ==  null){
					parent = "NOTFOUND";
				}
				
				if(this.potentialInheritenceRelations.keySet().contains(module)){
					this.potentialInheritenceRelations.get(module).add(start+"#"+end+"#"+parent+"#"+child);
				}else{
					Set<String>  aSet = new HashSet<String>();
					aSet.add(start+"#"+end+"#"+parent+"#"+child);
					this.potentialInheritenceRelations.put(module, aSet);
				}
			}			
		}
	}

	private String ExtractName(ParseTree aParseTree){
		String name;
		AbstractIdentifier id = null;
		if(aParseTree instanceof MemberExpressionTree){
			id = IdentifierHelper.getIdentifier(aParseTree.asMemberExpression());
			
		}else if(aParseTree instanceof IdentifierExpressionTree){
			id = IdentifierHelper.getIdentifier(aParseTree.asIdentifierExpression());
			
		}
		
		if(id!= null && id instanceof CompositeIdentifier){
			CompositeIdentifier compositName = id.asCompositeIdentifier();
			name = compositName.toString();
		}else{
			PlainIdentifier plianName = id.asPlainIdentifier();
			name = plianName.toString();
		}
		
		return name;
	}


}
