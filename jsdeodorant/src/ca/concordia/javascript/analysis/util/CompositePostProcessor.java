package ca.concordia.javascript.analysis.util;

import java.util.ArrayList;

import ca.concordia.javascript.analysis.abstraction.AnonymousFunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.Function;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;

public class CompositePostProcessor {
	public static void processFunctionDeclarations(Program program) {
		String fileHeader = "Invocation Type, DeclarationType, Number of Params, FunctionType";
		CSVFileWriter.writeToFile("log.csv", fileHeader.split(","));
		System.out.println("The number of creations in post processing: "
				+ program.getObjectCreations().size());
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			if (!findFunctionDeclaration(program, objectCreation))
				findAnonymousFunctionDeclaration(program, objectCreation);
		}
	}

	private static boolean findAnonymousFunctionDeclaration(Program program,
			ObjectCreation objectCreation) {
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
								.append(anonymousFunctionDeclaration.getName())
								.append(",")
								.append(anonymousFunctionDeclaration
										.getParameters().size()).append(",")
								.append("EXPRESSION");
						CSVFileWriter.writeToFile("log.csv", matchedLog
								.toString().split(","));
						return true;
					}
		}
		return false;
	}

	private static boolean findFunctionDeclaration(Program program,
			ObjectCreation objectCreation) {
		for (Function functionDeclaration : program.getFunctionDeclarations()) {
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
						return true;
					}
		}
		return false;
	}
}
