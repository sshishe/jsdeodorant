package ca.concordia.javascript.analysis.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.ClassDeclarationType;
import ca.concordia.javascript.analysis.abstraction.Function;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;

public class ExperimentOutput {
	static Logger log = Logger.getLogger(ExperimentOutput.class.getName());
	private Program program;
	private Set<String> matchedClassNames = new HashSet<>();
	private CSVFileWriter csvWriter;

	public ExperimentOutput(Program program) {
		this.program = program;
		csvWriter = new CSVFileWriter("log.csv");
		String fileHeader = "Invocation Type, DeclarationType, Number of Params, FunctionType,Invocation Location";
		csvWriter.writeToFile(fileHeader.split(","));
	}

	public void uniqueClassDeclarationNumber() {
		Set<Function> classes = new HashSet<>();
		log.info("Name and location of class declarations:");
		for (ObjectCreation creation : program.getObjectCreations()) {
			if (!classes.contains(creation.getClassDeclaration())) {
				if (creation.getClassDeclaration() != null) {
					classes.add(creation.getClassDeclaration());
					log.info(creation.getClassName()
							+ " "
							+ creation.getClassDeclaration()
									.getFunctionDeclarationTree().location);
				}
			}
		}
		log.info("Number of unique classes:" + classes.size());
	}

	public void writeToFile() {
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			if (objectCreation.getClassDeclarationType() == ClassDeclarationType.NOTFOUND) {
				StringBuilder unmatchedLog = new StringBuilder(objectCreation
						.getClassName().replace(",", "-"))
						.append(",")
						.append(objectCreation.getClassName().replace(",", "-"))
						.append(",")
						.append(objectCreation.getArguments().size())
						.append(",")
						.append(objectCreation.getClassDeclarationType())
						.append(",")
						.append(objectCreation.getNewExpressionTree().location
								.toString().replace(",", "-"));
				csvWriter.writeToFile(unmatchedLog.toString().split(","));
			} else if (objectCreation.getClassDeclarationType() == ClassDeclarationType.PREDEFINED) {
				matchedClassNames.add(objectCreation.getClassName());
				StringBuilder matchedLog = new StringBuilder(
						objectCreation.getClassName())
						.append(",")
						.append(objectCreation.getClassName())
						.append(",")
						.append(objectCreation.getArguments().size())
						.append(",")
						.append(objectCreation.getClassDeclarationType())
						.append(",")
						.append(objectCreation.getNewExpressionTree().location
								.toString().replace(",", "-"));
				csvWriter.writeToFile(matchedLog.toString().split(","));
			} else if (objectCreation.getClassDeclarationType() == ClassDeclarationType.ANONYMOUS) {
				matchedClassNames.add(objectCreation.getClassName());
				StringBuilder matchedLog = new StringBuilder(
						objectCreation.getClassName())
						.append(",")
						.append(objectCreation.getClassDeclaration().getName())
						.append(",")
						.append(objectCreation.getClassDeclaration()
								.getParameters().size())
						.append(",")
						.append(objectCreation.getClassDeclarationType())
						.append(",")
						.append(objectCreation.getNewExpressionTree().location
								.toString().replace(",", "-"));
				csvWriter.writeToFile(matchedLog.toString().split(","));
			} else if (objectCreation.getClassDeclarationType() == ClassDeclarationType.DECLARATION) {
				matchedClassNames.add(objectCreation.getClassName());
				StringBuilder matchedLog = new StringBuilder(
						objectCreation.getClassName())
						.append(",")
						.append(objectCreation.getClassDeclaration().getName())
						.append(",")
						.append(objectCreation.getClassDeclaration()
								.getParameters().size())
						.append(",")
						.append(objectCreation.getClassDeclarationType())
						.append(",")
						.append(objectCreation.getNewExpressionTree().location
								.toString().replace(",", "-"));
				csvWriter.writeToFile(matchedLog.toString().split(","));
			} else
				log.warn("Add the new type to experiment output");
		}
	}
}
