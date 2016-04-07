package ca.concordia.javascript.analysis;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.ObjectLiteralExpression;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.StringUtil;

public class ClassInferenceEngine {
	static Logger log = Logger.getLogger(ClassInferenceEngine.class.getName());

	public static void run(Program program) {
		for (FunctionDeclaration functionDeclaration : program.getFunctionDeclarationList()) {
			if (functionDeclaration.isClassDeclaration())
				continue;
			// angular.scenario.MyClass = ...
			assignedClassToACompositeNameWithPropsAndMethods(program, functionDeclaration);

			// function MockSpecRunner() {}
			// MockSpecRunner.prototype.run = function(spec, specDone) { ... }
			assignedMethodToProto(program, functionDeclaration);
		}
	}

	private static void assignedMethodToProto(Program program, FunctionDeclaration functionDeclaration) {
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
									break;
								}
							}
							AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) functionDeclaration;
							if (abstractFunctionFragment.getParent() instanceof ObjectLiteralExpression) {
								ObjectLiteralExpression objectLiteral = (ObjectLiteralExpression) abstractFunctionFragment.getParent();
								AbstractIdentifier identifier = objectLiteral.getIdentifier();
								if (identifier instanceof CompositeIdentifier) {
									if (identifier.asCompositeIdentifier().getMostRightPart().toString().equals("prototype")) {
										for (FunctionDeclaration functionToBeMatched : program.getFunctionDeclarationList()) {
											if (functionToBeMatched.getIdentifier() != null)
												if (functionToBeMatched.getIdentifier().toString().equals(((CompositeIdentifier) identifier).getMostLeftPart().toString())) {
													functionToBeMatched.setClassDeclaration(true);
													break;
												}
										}
									}
								}
							}
							if (abstractFunctionFragment instanceof FunctionDeclaration) {
								FunctionDeclaration functionToBeMatched = (FunctionDeclaration) abstractFunctionFragment;
								if (checkIfFunctionNameIsCapitalize(functionToBeMatched))
									functionToBeMatched.setClassDeclaration(true);

							}
							if (left.toString().contains("prototype"))
								log.warn(left.toString());
						}
					}
			}
		}
	}

	private static void assignedClassToACompositeNameWithPropsAndMethods(Program program, FunctionDeclaration functionDeclaration) {
		if (functionDeclaration.getRawIdentifier() != null)
			if (functionDeclaration.getRawIdentifier().toString().contains("prototype")) {
				log.warn(functionDeclaration.getRawIdentifier().toString());
				if (functionDeclaration.getRawIdentifier() instanceof CompositeIdentifier) {
					for (FunctionDeclaration functionToBeMatched : program.getFunctionDeclarationList()) {
						if (functionToBeMatched.getIdentifier() != null)
							if (functionToBeMatched.getIdentifier().toString().equals(functionDeclaration.getRawIdentifier().asCompositeIdentifier().getMostLeftPart().toString())) {
								if (checkIfFunctionNameIsCapitalize(functionDeclaration)) {
									functionToBeMatched.setClassDeclaration(true);
									break;
								}
							}
					}
				}
			}
	}

	private static boolean checkIfFunctionNameIsCapitalize(FunctionDeclaration function) {
		if (function.getIdentifier() != null)
			if (!StringUtil.isNullOrEmpty(function.getIdentifier().toString()))
				if (Character.isUpperCase(function.getIdentifier().toString().charAt(0)))
					return true;
		return false;
	}
}
