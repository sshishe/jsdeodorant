package ca.concordia.javascript.analysis;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.CSVFileWriter;
import ca.concordia.javascript.analysis.util.PredefinedJSClasses;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());
	private static CSVFileWriter csvWriter;

	public static void processFunctionDeclarations(Program program) {
		csvWriter = new CSVFileWriter("clasees.csv");
		String fileHeader = "Object creation, Function name, Obj Loc, Func Loc";
		csvWriter.writeToFile(fileHeader.split(","));

		for (ObjectCreation objectCreation : program.getObjectCreationList()) {
			if (objectCreation.getClassName() == null || objectCreation.isFunctionObject())
				continue;
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
		boolean findMatch = false;
		for (FunctionDeclaration functionDeclaration : program.getFunctionDeclarationList()) {
			String functionQualifiedName;
			if (functionDeclaration instanceof AbstractExpression) {
				functionQualifiedName = ((AbstractExpression) functionDeclaration).asIdentifiableExpression().getQualifiedName();
				//log.warn(((AbstractExpression) functionDeclaration).asIdentifiableExpression().getQualifiedName());
			} else
				functionQualifiedName = functionDeclaration.getName();
			writeEntriesToFile(objectCreation, functionDeclaration, functionQualifiedName);
			//log.info("Object creation name is: " + objectCreation.getIdentifier().toString() + " And function identifier is: " + functionQualifiedName);
			if (functionQualifiedName.equals(objectCreation.getIdentifier().toString())) {
				functionDeclaration.setClassDeclaration(true);
				objectCreation.setClassDeclaration(functionDeclaration);
				return true;
			}
		}
		return findMatch;
	}

	private static void writeEntriesToFile(ObjectCreation objectCreation, FunctionDeclaration functionDeclaration, String functionQualifiedName) {
		StringBuilder entry = new StringBuilder(objectCreation.getIdentifier().toString()).append(",").append(functionQualifiedName);
		entry.append(",");
		entry.append(objectCreation.getNewExpressionTree().location.toString().replace(",", "-"));
		entry.append(",");
		entry.append(functionDeclaration.getFunctionDeclarationTree().location.toString().replace(",", "-"));
		csvWriter.writeToFile(entry.toString().split(","));
	}
}
