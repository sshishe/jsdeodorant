package ca.concordia.javascript.analysis;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.CompositeIdentifier;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.ClassHelper;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.PredefinedJSClasses;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());

	public static void processFunctionDeclarations(Program program) {
		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (!findPredefinedClasses(program, objectCreation)) {
				findFunctionDeclaration(objectCreation, program);
			}
		}
	}

	private static boolean findPredefinedClasses(Program program, ObjectCreation objectCreation) {
		if (PredefinedJSClasses.contains(objectCreation.getClassName())) {
			objectCreation.setClassDeclarationPredefined(true);
			return true;
		}
		return false;
	}

	private static boolean findFunctionDeclaration(ObjectCreation objectCreation, Program program) {
		for (FunctionDeclaration functionDeclaration : program.getFunctionDeclarationList()) {
			AbstractIdentifier functionIdentifier = functionDeclaration.getIdentifier();
			if (functionIdentifier != null && objectCreation.getClassName() != null) {
				if (functionIdentifier.toString().equals(objectCreation.getIdentifier().toString())) {
					functionDeclaration.setClassDeclaration(true);
					objectCreation.addClassDeclaration(functionDeclaration);
				}
			}
		}
		// TODO Change based on recent changes to use Namespace object and
		// support Aliasing
		// for namespace detection
		if (objectCreation.getOperandOfNew() instanceof AbstractExpression)
			if (((AbstractExpression) objectCreation.getOperandOfNew()).getExpression() instanceof FunctionDeclarationTree) {
				// The creation is "new function(){...} which is not a class
				return false;
			}
		SourceContainer container = objectCreation.getStatement().getParent();
		List<FunctionDeclaration> functionDeclarationList = ClassHelper.getInnerFunctionList(container);
		boolean findMatch = matchCreationWithFunctionDeclarations(objectCreation.getIdentifier(), objectCreation, functionDeclarationList);
		if (!findMatch) {
			// Object literal
			// List<ObjectLiteralExpression> objectLiteralList = ClassHelper
			// .getObjectLiteralList(container);
			// matchCreationWithObjectLiterals(objectCreation.getIdentifier(),
			// objectCreation, objectLiteralList);
		}

		return findMatch;
	}

	private static boolean matchCreationWithFunctionDeclarations(AbstractIdentifier objectCreationIdentifier, ObjectCreation objectCreation, List<FunctionDeclaration> functions) {
		for (FunctionDeclaration functionDeclaration : functions) {
			if (objectCreationIdentifier instanceof PlainIdentifier) {
				if (objectCreationIdentifier.equals(ClassHelper.checkEqualsBasedOnNamespace(functionDeclaration))) {
					objectCreation.addClassDeclaration(functionDeclaration);
				}
			} else if (objectCreationIdentifier instanceof CompositeIdentifier) {
				CompositeIdentifier compositeIdentifier = (CompositeIdentifier) objectCreationIdentifier;
				List<FunctionDeclaration> innerFunctionList = null;
				// TODO normalizedIdentifier logic is too complex try to
				// refactor for nested namespaces
				AbstractIdentifier normalizedIdentifier = ClassHelper.checkEqualsBasedOnNamespace(functionDeclaration);
				if (normalizedIdentifier == null)
					normalizedIdentifier = functionDeclaration.getIdentifier();
				if (compositeIdentifier.equals(normalizedIdentifier))
					objectCreation.addClassDeclaration(functionDeclaration);
				else if (compositeIdentifier.getMostLeftPart().equals(normalizedIdentifier)) {
					innerFunctionList = ClassHelper.getInnerFunctionList(functionDeclaration);
					matchCreationWithFunctionDeclarations(compositeIdentifier.getRightPart(), objectCreation, innerFunctionList);
				} else {
					// for following scenario:
					// var a=new SomeClass();
					// var anotherClass=a.AnotherClass();
					SourceContainer parent = objectCreation.getStatement().getParent();
					List<ObjectCreation> objectCreations = null;
					if (parent instanceof Program) {
						objectCreations = ((Program) parent).getObjectCreationList();
					} else if (parent instanceof CompositeStatement) {
						objectCreations = ((AbstractFunctionFragment) parent).getObjectCreations();
					}
					for (ObjectCreation createdObject : objectCreations) {
						if (createdObject.getClassDeclaration() == null)
							continue;
						AbstractIdentifier lValue = IdentifierHelper.findLValue((AbstractStatement) createdObject.getStatement(), createdObject.getNewExpressionTree());
						if (lValue == null)
							continue;
						if (lValue.equals(compositeIdentifier.getMostLeftPart()))
							matchCreationWithFunctionDeclarations(compositeIdentifier.getRightPart(), objectCreation, ClassHelper.getInnerFunctionList(createdObject.getClassDeclaration()));
					}
				}
			}
		}
		return objectCreation.getClassDeclaration() != null;
	}
}
