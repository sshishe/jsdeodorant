package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.ObjectLiteralExpression;
import ca.concordia.javascript.analysis.util.PredefinedJSClasses;

public class Program implements SourceContainer {
	private static final Logger log = Logger.getLogger(Program.class.getName());
	private List<SourceElement> sourceElements;

	public Program() {
		sourceElements = new ArrayList<>();
	}

	public void addSourceElement(SourceElement source) {
		sourceElements.add(source);
		if (source instanceof AbstractStatement)
			log.debug(String.format("add %s to source program", ((AbstractStatement) source).getStatement().toString()));
		/*
		 * else if (source instanceof FunctionDeclaration)
		 * log.debug(String.format("add %s function to program",
		 * ((FunctionDeclaration) source).getName()));
		 */
	}

	@Override
	public void addElement(SourceElement element) {
		addSourceElement(element);
	}

	public List<SourceElement> getSourceElements() {
		return sourceElements;
	}

	public List<ObjectCreation> getObjectCreationList() {
		List<ObjectCreation> objectCreations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (Creation creation : abstractFunctionFragment.getCreations())
					if (creation instanceof ObjectCreation) {
						ObjectCreation objectCreation = (ObjectCreation) creation;
						if (!objectCreation.getOperandOfNew().toString().equalsIgnoreCase(PredefinedJSClasses.Array.toString()))
							objectCreations.add(objectCreation);
					}
			}
		}
		return objectCreations;
	}

	public List<ObjectCreation> getArrayCreationList() {
		List<ObjectCreation> objectCreations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (Creation creation : abstractFunctionFragment.getCreations())
					if (creation instanceof ObjectCreation) {
						ObjectCreation objectCreation = (ObjectCreation) creation;
						if (objectCreation.getOperandOfNew().toString().equalsIgnoreCase(PredefinedJSClasses.Array.toString()))
							objectCreations.add(objectCreation);
					}
			}
		}
		return objectCreations;
	}

	public List<ArrayLiteralCreation> getArrayLiteralCreationList() {
		List<ArrayLiteralCreation> arrayLiteralCreations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (Creation creation : abstractFunctionFragment.getCreations())
					if (creation instanceof ArrayLiteralCreation)
						arrayLiteralCreations.add((ArrayLiteralCreation) creation);
			}
		}
		return arrayLiteralCreations;
	}

	public List<ObjectLiteralExpression> getObjectLiteralList() {
		List<ObjectLiteralExpression> objectLiterals = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractStatement) {
				AbstractStatement statement = (AbstractStatement) sourceElement;
				objectLiterals.addAll(statement.getObjectLiteralList());
			}
		}
		return objectLiterals;
	}

	public List<FunctionDeclaration> getFunctionDeclarationList() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractStatement) {
				AbstractStatement statement = (AbstractStatement) sourceElement;
				functionDeclarations.addAll(statement.getFunctionDeclarationList());
			}
		}
		return functionDeclarations;
	}

	public List<FunctionInvocation> getFunctionInvocationList() {
		List<FunctionInvocation> functionInvocations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (FunctionInvocation functionInvocation : abstractFunctionFragment.getFunctionInvocationList())
					functionInvocations.add(functionInvocation);
				// TODO check if AnonymousFunctionDeclaration can be in the root
				// of program
			} else if (sourceElement instanceof FunctionInvocation)
				functionInvocations.add((FunctionInvocation) sourceElement);
		}
		return functionInvocations;
	}

	public List<VariableDeclaration> getVariableDeclarationList() {
		List<VariableDeclaration> variableDeclarations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractFunctionFragment) {
				AbstractFunctionFragment abstractFunctionFragment = (AbstractFunctionFragment) sourceElement;
				for (VariableDeclaration variableDeclaration : abstractFunctionFragment.getVariableDeclarationList())
					variableDeclarations.add(variableDeclaration);
			} else if (sourceElement instanceof VariableDeclaration)
				variableDeclarations.add((VariableDeclaration) sourceElement);
		}
		return variableDeclarations;
	}

	public List<FunctionDeclaration> getClassDeclarationList() {
		List<FunctionDeclaration> classDeclarations = new ArrayList<>();
		for (SourceElement sourceElement : sourceElements) {
			if (sourceElement instanceof AbstractStatement) {
				AbstractStatement statement = (AbstractStatement) sourceElement;
				for (FunctionDeclaration functionDeclaration : statement.getFunctionDeclarationList())
					if (functionDeclaration.isClassDeclaration())
						classDeclarations.add(functionDeclaration);
			}
		}
		return classDeclarations;
	}

}
