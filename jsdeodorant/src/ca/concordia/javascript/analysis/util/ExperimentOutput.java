package ca.concordia.javascript.analysis.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.AnalysisInstance;
import ca.concordia.javascript.analysis.AnalysisResult;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;

public class ExperimentOutput {
	static Logger log = Logger.getLogger(ExperimentOutput.class.getName());
	private AnalysisInstance analysisInstance;
	private CSVFileWriter csvWriter;

	public ExperimentOutput(AnalysisInstance analysisInstance) {
		this.analysisInstance = analysisInstance;
		csvWriter = new CSVFileWriter("log.csv");
		String fileHeader = "Invocation Type, DeclarationType, Number of Params, FunctionType,Invocation Location";
		csvWriter.writeToFile(fileHeader.split(","));
	}

	public void uniqueClassDeclarationNumber() {
		Set<FunctionDeclaration> classes = new HashSet<>();
		for (ObjectCreation creation : analysisInstance.getProgram().getObjectCreationList()) {
			if (!classes.contains(creation.getClassDeclaration())) {
				if (creation.getClassDeclaration() != null) {
					classes.add(creation.getClassDeclaration());
					log.info(creation.getClassName() + " " + creation.getClassDeclaration().getFunctionDeclarationTree().location + "And the invocation is at: " + creation.getNewExpressionTree().location);
				}
			}
		}
		if (classes.size() > 0) {
			log.info("Number of unique classes in this file:" + classes.size());
			AnalysisResult.setTotalNumberOfClasses(AnalysisResult.getTotalNumberOfClasses() + classes.size());
		}
	}

	public void writeToFile() {
		// for (ObjectCreation objectCreation : program.getObjectCreations()) {
		// if (objectCreation.getClassDeclarationType() ==
		// ClassDeclarationType.NOTFOUND) {
		// StringBuilder unmatchedLog = new StringBuilder(objectCreation
		// .getClassName().replace(",", "-"))
		// .append(",")
		// .append(objectCreation.getClassName().replace(",", "-"))
		// .append(",")
		// .append(objectCreation.getArguments().size())
		// .append(",")
		// .append(objectCreation.getClassDeclarationType())
		// .append(",")
		// .append(objectCreation.getNewExpressionTree().location
		// .toString().replace(",", "-"));
		// csvWriter.writeToFile(unmatchedLog.toString().split(","));
		// } else if (objectCreation.getClassDeclarationType() ==
		// ClassDeclarationType.PREDEFINED) {
		// matchedClassNames.add(objectCreation.getClassName());
		// StringBuilder matchedLog = new StringBuilder(
		// objectCreation.getClassName())
		// .append(",")
		// .append(objectCreation.getClassName())
		// .append(",")
		// .append(objectCreation.getArguments().size())
		// .append(",")
		// .append(objectCreation.getClassDeclarationType())
		// .append(",")
		// .append(objectCreation.getNewExpressionTree().location
		// .toString().replace(",", "-"));
		// csvWriter.writeToFile(matchedLog.toString().split(","));
		// }
		// // } else if (objectCreation.getClassDeclarationType() ==
		// // ClassDeclarationType.ANONYMOUS) {
		// // matchedClassNames.add(objectCreation.getClassName());
		// // StringBuilder matchedLog = new StringBuilder(
		// // objectCreation.getClassName())
		// // .append(",")
		// // .append(objectCreation.getClassDeclaration().getName())
		// // .append(",")
		// // .append(objectCreation.getClassDeclaration()
		// // .getParameters().size())
		// // .append(",")
		// // .append(objectCreation.getClassDeclarationType())
		// // .append(",")
		// // .append(objectCreation.getNewExpressionTree().location
		// // .toString().replace(",", "-"));
		// // csvWriter.writeToFile(matchedLog.toString().split(","));
		// // }
		// else if (objectCreation.getClassDeclarationType() ==
		// ClassDeclarationType.FUNCTION) {
		// matchedClassNames.add(objectCreation.getClassName());
		// StringBuilder matchedLog = new StringBuilder(
		// objectCreation.getClassName())
		// .append(",")
		// .append(objectCreation.getClassDeclaration().getName())
		// .append(",")
		// .append(objectCreation.getClassDeclaration()
		// .getParameters().size())
		// .append(",")
		// .append(objectCreation.getClassDeclarationType())
		// .append(",")
		// .append(objectCreation.getNewExpressionTree().location
		// .toString().replace(",", "-"));
		// csvWriter.writeToFile(matchedLog.toString().split(","));
		// } else
		// log.warn("Add the new type to experiment output");
		// }
	}
}
