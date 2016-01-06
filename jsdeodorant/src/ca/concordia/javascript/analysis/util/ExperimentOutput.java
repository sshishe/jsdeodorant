package ca.concordia.javascript.analysis.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.AnalysisResult;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;

public class ExperimentOutput {
	static Logger log = Logger.getLogger(ExperimentOutput.class.getName());
	private Module currentModule;
	private CSVFileWriter csvWriter;

	public ExperimentOutput() {
	}

	public ExperimentOutput(Module module) {
		this.currentModule = module;
	}

	public void functionSignatures() {
		if (currentModule.getProgram().getFunctionDeclarationList().size() == 0)
			return;
		csvWriter = new CSVFileWriter("log/functions/" + getFileName() + ".csv");
		String fileHeader = "File path, function name, FunctionType, Is it a library,Declaration Location, Number of Params,Parameter Names, Number of Return Statements";
		csvWriter.writeToFile(fileHeader.split(","));
		for (FunctionDeclaration functionDeclaration : currentModule.getProgram().getFunctionDeclarationList()) {
			StringBuilder lineToWrite = new StringBuilder();
			int parameterSize = functionDeclaration.getParameters().size();
			String parametersName = getParametersName(functionDeclaration);
			lineToWrite.append(currentModule.getSourceFile().getName()).append(",").append(functionDeclaration.getName().replace(",", "-")).append(",").append(functionDeclaration.getKind()).append(",").append(currentModule.isLibrary()).append(",").append(functionDeclaration.getFunctionDeclarationTree().location.toString().replace(",", "-")).append(",").append(parameterSize).append(",").append(parametersName).append(",").append(functionDeclaration.getReturnStatementList().size());
			csvWriter.writeToFile(lineToWrite.toString().split(","));
		}

	}

	public void uniqueClassDeclaration() {
		if (currentModule.getProgram().getObjectCreationList().size() == 0)
			return;
		String currentFilePath = "log/classes/" + getFileName() + ".csv";
		csvWriter = new CSVFileWriter(currentFilePath);
		String fileHeader = "Object Creation Name, Class FQN,DeclarationType, Number of Arguments, Number of Parameters, Parameter Names, Invocation Location, Declaration Location";
		csvWriter.writeToFile(fileHeader.split(","));
		Set<FunctionDeclaration> classes = new HashSet<>();
		for (ObjectCreation creation : currentModule.getProgram().getObjectCreationList()) {
			if (!classes.contains(creation.getClassDeclaration())) {
				if (creation.getClassDeclaration() != null) {
					classes.add(creation.getClassDeclaration());
					writeClassDeclarationToFile(creation);
					log.info(creation.getClassName() + " " + creation.getClassDeclaration().getFunctionDeclarationTree().location + " And the invocation is at: " + creation.getNewExpressionTree().location);
				}
			}
		}
		if (classes.size() == 0) {
			new File(currentFilePath).delete();
			return;
		} else
			AnalysisResult.increaseTotalNumberOfFiles();

		if (classes.size() > 0) {
			log.info("Number of unique classes in this file:" + classes.size());
			AnalysisResult.increaseTotalNumberOfClasses(classes.size());
		}
	}

	public void aggregateReportForModule(List<Module> modules) {
		String currentFilePath = "log/aggregate/modules.csv";
		csvWriter = new CSVFileWriter(currentFilePath);
		String fileHeader = "Module name, Is it a library, Number of dependencies, Number of exports";
		csvWriter.writeToFile(fileHeader.split(","));
		for (Module module : modules) {
			StringBuilder lineToWrite = new StringBuilder();
			lineToWrite.append(module.getSourceFile().getName()).append(",").append(module.isLibrary()).append(",").append(module.getDependencies().size()).append(",").append(module.getExports().size());
			csvWriter.writeToFile(lineToWrite.toString().split(","));
		}
	}

	private void writeClassDeclarationToFile(ObjectCreation objectCreation) {
		StringBuilder lineToWrite = new StringBuilder();
		int parameterSize = objectCreation.getClassDeclaration().getParameters().size();
		String parametersName = getParametersName(objectCreation.getClassDeclaration());
		lineToWrite.append(objectCreation.getClassName().replace(",", "-")).append(",").append(objectCreation.getClassDeclaration().getQualifiedName()).append(",").append(objectCreation.getClassDeclaration().getKind()).append(",").append(objectCreation.getArguments().size()).append(",").append(parameterSize).append(",").append(parametersName).append(",").append(objectCreation.getNewExpressionTree().location.toString().replace(",", "-")).append(",").append(objectCreation.getClassDeclaration().getFunctionDeclarationTree().location.toString().replace(",", "-"));
		csvWriter.writeToFile(lineToWrite.toString().split(","));
	}

	private String getParametersName(FunctionDeclaration functionDeclaration) {
		int parameterSize = functionDeclaration.getParameters().size();
		if (parameterSize > 0) {
			StringBuilder parameters = new StringBuilder();
			int parameterIndex = 0;
			for (AbstractExpression parameter : functionDeclaration.getParameters()) {
				parameters.append(IdentifierHelper.getIdentifier(parameter.getExpression()));
				if (parameterIndex < parameterSize - 1)
					parameters.append("|");
				parameterIndex++;
			}
			return parameters.toString();
		}
		return "";
	}

	public static boolean createAndClearFolder(String folderName) {
		File directory = new File(folderName);
		if (!directory.exists()) {
			directory.mkdirs();
			return true;
		}
		// If folder already exists, remove all files
		for (File file : directory.listFiles())
			file.delete();
		return true;
	}

	private String getFileName() {
		String[] filePart = currentModule.getSourceFile().getOriginalPath().split("/");
		String fileName = FileUtil.getElementsOf(filePart, filePart.length - 2, filePart.length - 1).replace("/", "|");
		if (fileName.lastIndexOf('|') == fileName.length() - 1)
			fileName = fileName.substring(0, fileName.length() - 1);
		return fileName;
	}
}
