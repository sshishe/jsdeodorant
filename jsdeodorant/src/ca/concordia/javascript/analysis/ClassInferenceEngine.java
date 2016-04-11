package ca.concordia.javascript.analysis;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.ClassDeclaration;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.decomposition.ObjectLiteralExpression;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class ClassInferenceEngine {
	static Logger log = Logger.getLogger(ClassInferenceEngine.class.getName());

	public static void run(Module module) {
		for (FunctionDeclaration functionDeclaration : module.getProgram().getFunctionDeclarationList()) {
			if (functionDeclaration.isClassDeclaration())
				continue;
			// angular.scenario.MyClass = ...
			assignedClassToACompositeNameWithPropsAndMethods(module, functionDeclaration);

			// function MockSpecRunner() {}
			// MockSpecRunner.prototype.run = function(spec, specDone) { ... }
			assignedMethodToProto(module, functionDeclaration);

			assignObjectLiteralToPrototype(module, functionDeclaration);
		}

		nowSetClassesToNotFoundObjectCreations(module);
	}

	private static void nowSetClassesToNotFoundObjectCreations(Module module) {
		for (ObjectCreation objectCreation : module.getProgram().getObjectCreationList()) {
			if (objectCreation.getClassDeclaration() != null)
				for (ClassDeclaration classDeclaration : module.getClasses()) {
					if (objectCreation.getIdentifier().equals(classDeclaration.getName()))
						objectCreation.setClassDeclaration(classDeclaration, module);
				}
		}
	}

	private static void assignObjectLiteralToPrototype(Module module, FunctionDeclaration functionDeclaration) {
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
			return;

		for (AbstractExpression assignmentExpression : assignmentList) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				AbstractIdentifier left = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
				if (left instanceof CompositeIdentifier) {
					if (functionDeclaration.getName().equals(left.asCompositeIdentifier().getLeftPart().toString()))
						if (((CompositeIdentifier) left).getMostRightPart().toString().contains("prototype")) {
							boolean hasNamespace = false;
							if (functionDeclaration instanceof FunctionDeclarationExpression)
								hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();

							ClassDeclaration classDeclaration = new ClassDeclaration(functionDeclaration.getIdentifier(), functionDeclaration, true, hasNamespace);
							module.addClass(classDeclaration);
						}
				}
			}
		}
	}

	private static void assignedMethodToProto(Module module, FunctionDeclaration functionDeclaration) {
		for (AbstractExpression assignmentExpression : functionDeclaration.getAssignments()) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				AbstractIdentifier left = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
				if (left instanceof CompositeIdentifier)
					if (left.asCompositeIdentifier().toString().contains("this.")) {
						if (functionDeclaration instanceof AbstractFunctionFragment) {
							if (functionDeclaration.getRawIdentifier() instanceof CompositeIdentifier) {
								CompositeIdentifier compositeIdentifier = functionDeclaration.getRawIdentifier().asCompositeIdentifier();
								if (Character.isUpperCase(compositeIdentifier.getMostRightPart().toString().charAt(0))) {
									functionDeclaration.setClassDeclaration(true);

									boolean hasNamespace = false;
									if (functionDeclaration instanceof FunctionDeclarationExpression)
										hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();

									ClassDeclaration classDeclaration = new ClassDeclaration(functionDeclaration.getIdentifier(), functionDeclaration, true, hasNamespace);
									module.addClass(classDeclaration);
									break;
								}
							}
							AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) functionDeclaration;
							if (abstractFunctionFragment.getParent() instanceof ObjectLiteralExpression) {
								ObjectLiteralExpression objectLiteral = (ObjectLiteralExpression) abstractFunctionFragment.getParent();
								AbstractIdentifier identifier = objectLiteral.getIdentifier();
								if (identifier instanceof CompositeIdentifier) {
									if (identifier.asCompositeIdentifier().getMostRightPart().toString().equals("prototype")) {
										for (FunctionDeclaration functionToBeMatched : module.getProgram().getFunctionDeclarationList()) {
											if (functionToBeMatched.getIdentifier() != null)
												if (functionToBeMatched.getIdentifier().toString().equals(((CompositeIdentifier) identifier).getMostLeftPart().toString())) {
													functionToBeMatched.setClassDeclaration(true);

													boolean hasNamespace = false;
													if (functionDeclaration instanceof FunctionDeclarationExpression)
														hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();

													ClassDeclaration classDeclaration = new ClassDeclaration(functionToBeMatched.getIdentifier(), functionToBeMatched, true, hasNamespace);
													module.addClass(classDeclaration);
													break;
												}
										}
									}
								}
							}
							//							if (abstractFunctionFragment instanceof FunctionDeclaration) {
							//								FunctionDeclaration functionToBeMatched = (FunctionDeclaration) abstractFunctionFragment;
							//								if (checkIfFunctionNameIsCapitalize(functionToBeMatched)) {
							//									functionToBeMatched.setClassDeclaration(true);
							//									ClassDeclaration classDeclaration = new ClassDeclaration(functionToBeMatched.getName(), functionToBeMatched, true);
							//									module.addClass(classDeclaration);
							//								}
							//							}
							//							if (left.toString().contains("prototype"))
							//								log.warn(left.toString());
						}
					}
			}
		}
	}

	private static void assignedClassToACompositeNameWithPropsAndMethods(Module module, FunctionDeclaration functionDeclaration) {
		if (functionDeclaration.getRawIdentifier() != null)
			if (functionDeclaration.getRawIdentifier().toString().contains("prototype")) {
				//	log.warn(functionDeclaration.getRawIdentifier().toString());
				if (functionDeclaration.getRawIdentifier() instanceof CompositeIdentifier) {
					for (FunctionDeclaration functionToBeMatched : module.getProgram().getFunctionDeclarationList()) {
						if (functionToBeMatched.getIdentifier() != null)
							if (functionToBeMatched.getIdentifier().toString().equals(functionDeclaration.getRawIdentifier().asCompositeIdentifier().getMostLeftPart().toString())) {
								if (checkIfFunctionNameIsCapitalize(functionDeclaration)) {
									functionToBeMatched.setClassDeclaration(true);

									boolean hasNamespace = false;
									if (functionDeclaration instanceof FunctionDeclarationExpression)
										hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();

									ClassDeclaration classDeclaration = new ClassDeclaration(functionToBeMatched.getIdentifier(), functionToBeMatched, true, hasNamespace);
									module.addClass(classDeclaration);
									break;
								}
							}
					}
				}
			}
	}

	private static boolean checkIfFunctionNameIsCapitalize(FunctionDeclaration function) {
		return true;
		//		if (function.getIdentifier() != null)
		//			if (!StringUtil.isNullOrEmpty(function.getIdentifier().toString()))
		//				if (Character.isUpperCase(function.getIdentifier().toString().charAt(0)))
		//					return true;
		//		log.warn("The founded class is not first letter capitalized!");
		//		return false;
	}

	public static void analyzeMethodsAndAttributes(Module module) {
		for (ClassDeclaration classDeclaration : module.getClasses()) {
			analyzeMethodsAndAttributes(classDeclaration, module);
		}
	}

	private static void analyzeMethodsAndAttributes(ClassDeclaration classDeclaration, Module module) {
		FunctionDeclaration functionDeclaration = classDeclaration.getFunctionDeclaration();
		// Lookup for attributes and methods inside function
		for (AbstractExpression assignmentExpression : functionDeclaration.getAssignments()) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				AbstractIdentifier left = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
				if (left instanceof CompositeIdentifier) {
					if (left.asCompositeIdentifier().toString().contains("this.")) {
						if (binaryOperatorTree.right instanceof FunctionDeclarationTree) {
							// Then, it's method
							classDeclaration.addMethod(left.asCompositeIdentifier().getRightPart().toString(), assignmentExpression);
						} else {
							// It's attribute
							classDeclaration.addAttribtue(left.asCompositeIdentifier().getRightPart().toString(), assignmentExpression);
						}
					}
				}
			}
		}

		AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) functionDeclaration;
		SourceContainer parent = abstractFunctionFragment.getParent();
		List<AbstractExpression> assignmentList = null;
		List<ObjectLiteralExpression> objectLiteralExpressionList = null;
		if (parent instanceof Program) {
			Program program = (Program) parent;
			assignmentList = program.getAssignmentExpressionList();
			objectLiteralExpressionList = program.getObjectLiteralList();
		} else if (parent instanceof CompositeStatement) {
			CompositeStatement compositeParent = (CompositeStatement) parent;
			assignmentList = compositeParent.getAssignmentExpressionList();
			objectLiteralExpressionList = compositeParent.getObjectLiteralList();
		}

		if (assignmentList == null)
			return;

		for (AbstractExpression assignmentExpression : assignmentList) {
			if (assignmentExpression.getExpression() instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperatorTree = assignmentExpression.getExpression().asBinaryOperator();
				AbstractIdentifier left = IdentifierHelper.getIdentifier(binaryOperatorTree.left);
				if (left instanceof CompositeIdentifier) {
					if (classDeclaration.getName().equals(left.asCompositeIdentifier().getMostLeftPart().toString()))
						if (((CompositeIdentifier) left).getRightPart().toString().contains("prototype")) {
							if (binaryOperatorTree.right instanceof ObjectLiteralExpressionTree) {
								ObjectLiteralExpressionTree objectLiteralExpression = binaryOperatorTree.right.asObjectLiteralExpression();
								for (ObjectLiteralExpression objExpression : objectLiteralExpressionList) {
									if (objExpression.getExpression().equals(objectLiteralExpression)) {
										extractMethodsFromObjectLiteral(objExpression, classDeclaration, module);
									}
								}
							}
							if (binaryOperatorTree.right instanceof FunctionDeclarationTree) {
								// Then, it's method
								classDeclaration.addMethod(left.asCompositeIdentifier().getMostRightPart().toString(), assignmentExpression);
							} else {
								// It's attribute
								//classDeclaration.addAttribtue(left.asCompositeIdentifier().getRightPart().toString(), assignmentExpression);
							}
						}
				}
			}
		}

		if (abstractFunctionFragment.getParent() instanceof ObjectLiteralExpression) {
			ObjectLiteralExpression objectLiteral = (ObjectLiteralExpression) abstractFunctionFragment.getParent();
			AbstractIdentifier identifier = objectLiteral.getIdentifier();
			if (identifier instanceof CompositeIdentifier) {
				if (identifier.asCompositeIdentifier().getMostRightPart().toString().equals("prototype")) {
					for (FunctionDeclaration functionToBeMatched : module.getProgram().getFunctionDeclarationList()) {
						if (functionToBeMatched.getIdentifier() != null)
							if (functionToBeMatched.getIdentifier().toString().equals(((CompositeIdentifier) identifier).getMostLeftPart().toString())) {
								functionToBeMatched.setClassDeclaration(true);
								break;
							}
					}
				}
			}
		}
		//					if (abstractFunctionFragment instanceof FunctionDeclaration) {
		//						FunctionDeclaration functionToBeMatched = (FunctionDeclaration) abstractFunctionFragment;
		//						if (checkIfFunctionNameIsCapitalize(functionToBeMatched))
		//							functionToBeMatched.setClassDeclaration(true);
		//
		//					}
	}

	private static void extractMethodsFromObjectLiteral(ObjectLiteralExpression objExpression, ClassDeclaration classDeclaration, Module module) {
		Map<Token, AbstractExpression> propertyMap = objExpression.getPropertyMap();
		for (Token key : propertyMap.keySet()) {
			AbstractExpression value = propertyMap.get(key);
			if (value.getExpression() instanceof FunctionDeclarationTree) {
				classDeclaration.addMethod(key.toString(), value);
			}
		}

	}
}
