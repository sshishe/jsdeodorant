package ca.concordia.javascript.analysis.util;

import ca.concordia.javascript.analysis.abstraction.AnonymousFunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.Function;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;

public class CompositePostProcessor {
	public static void processFunctionDeclarations(Program program) {
		String fileHeader = "Invocation Type, DeclarationType, Number of Params, FunctionType";
		CSVFileWriter.writeToFile("log.csv", fileHeader.split(","));
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			for (Function functionDeclaration : program
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

				if (objectCreation.getClassName() != null)
					if (objectCreation.getClassName().equals(
							anonymousFunctionDeclaration.getName()))
						if (objectCreation.getArguments().size() == anonymousFunctionDeclaration
								.getParameters().size()) {
							objectCreation
									.setFunctionDeclaration(anonymousFunctionDeclaration);
							StringBuilder matchedLog = new StringBuilder(
									objectCreation.getClassName())
									.append(",")
									.append(anonymousFunctionDeclaration
											.getName())
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
