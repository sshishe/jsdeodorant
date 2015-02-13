package ca.concordia.javascript.analysis.util;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.ClassDeclarationType;
import ca.concordia.javascript.analysis.abstraction.Function;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.abstraction.Program;

public class CompositePostProcessor {
	static Logger log = Logger
			.getLogger(CompositePostProcessor.class.getName());

	public static void processFunctionDeclarations(Program program) {
		for (ObjectCreation objectCreation : program.getObjectCreations()) {
			if (!findPredefinedClasses(program, objectCreation))
				if (!findFunctionDeclaration(program, objectCreation))
					if (!findAnonymousFunctionDeclaration(program,
							objectCreation)) {
						objectCreation
								.setClassDeclaration(ClassDeclarationType.NOTFOUND);
					}
		}
	}

	private static boolean findPredefinedClasses(Program program,
			ObjectCreation objectCreation) {
		if (PredefinedJSClasses.contains(objectCreation.getClassName())) {
			objectCreation.setClassDeclaration(ClassDeclarationType.PREDEFINED);
			return true;
		}
		return false;
	}

	private static boolean findAnonymousFunctionDeclaration(Program program,
			ObjectCreation objectCreation) {
		for (Function anonymousFunctionDeclaration : program
				.getAnonymousFunctionDeclarations()) {
			if (objectCreation.getClassName() != null)
				if (objectCreation.getClassName().equals(
						anonymousFunctionDeclaration.getName()))
					if (objectCreation.getArguments().size() == anonymousFunctionDeclaration
							.getParameters().size()) {
						objectCreation.setClassDeclaration(
								ClassDeclarationType.ANONYMOUS,
								anonymousFunctionDeclaration);
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
						objectCreation.setClassDeclaration(
								ClassDeclarationType.DECLARATION,
								functionDeclaration);
						return true;
					}
		}
		return false;
	}
}
