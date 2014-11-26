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
		String fileHeader = "Invocation Type, DeclarationType, Number of Params, FunctionType";
		CSVFileWriter.writeToFile("log.csv", fileHeader.split(","));
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			for (FunctionDeclaration functionDeclaration : program
					.getFunctionDeclarations()) {
				if (objectCreation.getClassName() != null)
					if (objectCreation.getClassName().equals(
							functionDeclaration.getName()))
						if (objectCreation.getArguments().size() == functionDeclaration
								.getParameters().size()) {
							objectCreation
									.setFunctionDeclaration(functionDeclaration);
							StringBuilder matchedLog = new StringBuilder(
									objectCreation.getClassName())
									.append(",")
									.append(functionDeclaration.getName())
									.append(",")
									.append(functionDeclaration.getParameters()
											.size()).append(",")
									.append("DECLARATION");

							CSVFileWriter.writeToFile("log.csv", matchedLog
									.toString().split(","));
							System.out.println(matchedLog);
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
				if (objectCreation.getClassName() != null)
					if (objectCreation.getClassName().equals(tokenName))
						if (objectCreation.getArguments().size() == anonymousFunctionDeclaration
								.getParameters().size()) {
							objectCreation
									.setFunctionDeclaration(anonymousFunctionDeclaration);
							StringBuilder matchedLog = new StringBuilder(
									objectCreation.getClassName())
									.append(",")
									.append(tokenName)
									.append(",")
									.append(anonymousFunctionDeclaration
											.getParameters().size())
									.append(",").append("EXPRESSION");
							CSVFileWriter.writeToFile("log.csv", matchedLog
									.toString().split(","));
							System.out.println(matchedLog);
						}
			}
		}
	}
}
