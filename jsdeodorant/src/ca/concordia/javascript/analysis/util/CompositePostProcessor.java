package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.AnonymousFunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.FunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;

public class CompositePostProcessor {

	public static void processFunctionDeclarations(Program program) {
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			for (FunctionDeclaration functionDeclaration : program
					.getFunctionDeclarations()) {
				if (objectCreation.getClassName().equals(
						functionDeclaration.getName()))
					if (objectCreation.getArguments().size() == functionDeclaration
							.getParameters().size()) {
						objectCreation
								.setFunctionDeclaration(functionDeclaration);
						System.out.println("The created type is: "
								+ objectCreation.getClassName()
								+ " The function declaration name is: "
								+ functionDeclaration.getName()
								+ " and the number of params are: "
								+ functionDeclaration.getParameters().size());
					}
			}
			for (AnonymousFunctionDeclaration anonymousFunctionDeclaration : program
					.getAnonymousFunctionDeclarations()) {
				String tokenName = null;
				ParseTree expression = anonymousFunctionDeclaration
						.getLeftOperand().getExpression();
				if (expression instanceof IdentifierExpressionTree)
					tokenName = expression.asIdentifierExpression().identifierToken.value;
				else if (expression instanceof MemberExpressionTree)
					tokenName = expression.asMemberExpression().memberName.value;
				if (objectCreation.getClassName().equals(tokenName))
					if (objectCreation.getArguments().size() == anonymousFunctionDeclaration
							.getFunctionDeclaration().getParameters().size()) {
						objectCreation
								.setFunctionDeclaration(anonymousFunctionDeclaration);
						System.out
								.println("The created type is: "
										+ objectCreation.getClassName()
										+ " The property name that anonymous function expression assigned is: "
										+ tokenName
										+ " and the number of params are: "
										+ anonymousFunctionDeclaration
												.getFunctionDeclaration()
												.getParameters().size());
					}
			}
		}
	}
}
