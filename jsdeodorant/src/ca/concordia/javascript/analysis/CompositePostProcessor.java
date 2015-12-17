package ca.concordia.javascript.analysis;

import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.Statement;
import ca.concordia.javascript.analysis.util.CSVFileWriter;
import ca.concordia.javascript.analysis.util.ModuleHelper;
import ca.concordia.javascript.analysis.util.PredefinedJSClasses;

public class CompositePostProcessor {
	static Logger log = Logger.getLogger(CompositePostProcessor.class.getName());
	private static CSVFileWriter csvWriter;

	public static void processFunctionDeclarationsToFindClasses(Module packageInstance) {
		Program program = packageInstance.getProgram();
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

	public static void processModules(Module module, List<Module> modules) {
		ModuleHelper moduleHelper = new ModuleHelper();
		Program program = module.getProgram();
		for (SourceElement element : program.getSourceElements()) {
			if (element instanceof Statement) {
				Statement statement = (Statement) element;
				if (moduleHelper.hasRequireStatement(module, modules, statement.getStatement()))
					log.warn(element);
			}
		}
		module.setName("shahriar");
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
			String functionQualifiedName = functionDeclaration.getQualifiedName();
			//log.info("Object creation name is: " + objectCreation.getAliasedIdentifier().toString() + " And function identifier is: " + functionQualifiedName);
			if (functionQualifiedName.equals(objectCreation.getIdentifier().toString())) {
				//if (functionQualifiedName.equals(objectCreation.getIdentifier().toString())) {
				functionDeclaration.setClassDeclaration(true);
				objectCreation.setClassDeclaration(functionDeclaration);
				return true;
			}
		}
		return findMatch;
	}
}
