package ca.concordia.javascript.experiment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.decomposition.ClassDeclaration;
import ca.concordia.javascript.analysis.util.SourceLocationHelper;

public class ClassAnalysisReport {
	static Logger log = Logger.getLogger(ClassAnalysisReport.class.getName());
	private static List<ClassInstance> classes;

	private static void checkForInitialization() {
		if (classes == null)
			classes = new ArrayList<>();
	}

	public static void updateReport(List<Module> modules) {
		checkForInitialization();
		for (Module module : modules) {
			for (ClassDeclaration classDeclaration : module.getClasses()) {
				addClass(classDeclaration, module);
			}
		}
	}

	public static void addClass(ClassDeclaration classDeclaration, Module module) {
		ClassInstance classInstance = new ClassInstance(module, classDeclaration);
		classInstance.setClassName(classDeclaration.getName());
		classInstance.setClassOffset(SourceLocationHelper.getLocation(classDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location));
		classInstance.setClassLOC(classDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location.start.line);
		classInstance.setPredefined(false);
		classInstance.setHasNewExpression(false);
		classInstance.setHasInfered(true);
		classInstance.setHasNamespace(classDeclaration.hasNamespace());
		add(classInstance);
	}

	public static void addPredefinedClass(ObjectCreation creation, Module module) {
		ClassInstance classInstance = new ClassInstance(module, creation.getClassDeclaration());
		classInstance.setClassName(creation.getIdentifier().toString());
		classInstance.setPredefined(true);
		//		classInstance.setNewExpressionFile(module.getCanonicalPath());
		//		classInstance.setNewExpressionOffset(SourceLocationHelper.getLocation(creation.getOperandOfNew().getExpression().location));
		classInstance.setHasNewExpression(true);
		classInstance.setHasInfered(false);
		classInstance.setHasNamespace(false);
		add(classInstance);
	}

	public static void add(ClassInstance instance) {
		for (ClassInstance classInstance : classes) {
			if (!instance.isPredefined() && !classInstance.isPredefined())
				if (classInstance.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree().equals(instance.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree())) {
					if (classInstance.hasNewExpression)
						classInstance.incrementClassInstantiation();
					return;
				}
		}
		classes.add(instance);
	}

	public static List<ClassInstance> getList() {
		return classes;
	}

	public static int getClassCount() {
		return classes.size();
	}

	public static ClassInstance get(String className, String file) {
		for (ClassInstance classInstance : classes) {
			if (classInstance.getClassName().equals(className) && classInstance.getFileName().equals(file))
				return classInstance;
		}
		return null;
	}

	public static class ClassInstance {
		private ClassDeclaration classDeclaration;
		private String className;
		private String fileName;
		private boolean isPredefined;
		private String classOffset;
		private boolean hasNewExpression;
		private boolean hasInfered;
		private int constructorLOC;
		private int classLOC;
		private boolean hasNamespace;
		private String namespace;
		private int numberOfMethods;
		private int numberOfAttributes;
		private int numberOfParameters;
		private int isDeclarationInLibrary;
		private boolean isAliased;
		private int numberOfInstantiation;

		public ClassInstance(Module module, ClassDeclaration classDeclaration) {
			ClassAnalysisReport.checkForInitialization();
			this.classDeclaration = classDeclaration;
			this.fileName = module.getSourceFile().getOriginalPath();
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getClassOffset() {
			return classOffset;
		}

		public void setClassOffset(String classOffset) {
			this.classOffset = classOffset;
		}

		public boolean isHasNewExpression() {
			return hasNewExpression;
		}

		public void setHasNewExpression(boolean hasNewExpression) {
			this.hasNewExpression = hasNewExpression;
		}

		public boolean isHasInfered() {
			return hasInfered;
		}

		public void setHasInfered(boolean hasInfered) {
			this.hasInfered = hasInfered;
		}

		//		public String getNewExpressionFile() {
		//			return newExpressionFile;
		//		}
		//
		//		public void setNewExpressionFile(String newExpressionFile) {
		//			this.newExpressionFile = newExpressionFile;
		//		}
		//
		//		public String getNewExpressionOffset() {
		//			return newExpressionOffset;
		//		}
		//
		//		public void setNewExpressionOffset(String newExpressionOffset) {
		//			this.newExpressionOffset = newExpressionOffset;
		//		}

		public int getConstructorLOC() {
			return constructorLOC;
		}

		public void setConstructorLOC(int constructorLOC) {
			this.constructorLOC = constructorLOC;
		}

		public int getClassLOC() {
			return classLOC;
		}

		public void setClassLOC(int classLOC) {
			this.classLOC = classLOC;
		}

		public boolean isHasNamespace() {
			return hasNamespace;
		}

		public void setHasNamespace(boolean hasNamespace) {
			this.hasNamespace = hasNamespace;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public int getNumberOfMethods() {
			return numberOfMethods;
		}

		public void setNumberOfMethods(int numberOfMethods) {
			this.numberOfMethods = numberOfMethods;
		}

		public int getNumberOfAttributes() {
			return numberOfAttributes;
		}

		public void setNumberOfAttributes(int numberOfAttributes) {
			this.numberOfAttributes = numberOfAttributes;
		}

		public int getNumberOfParameters() {
			return numberOfParameters;
		}

		public void setNumberOfParameters(int numberOfParameters) {
			this.numberOfParameters = numberOfParameters;
		}

		public int getIsDeclarationInLibrary() {
			return isDeclarationInLibrary;
		}

		public void setIsDeclarationInLibrary(int isDeclarationInLibrary) {
			this.isDeclarationInLibrary = isDeclarationInLibrary;
		}

		public boolean isAliased() {
			return isAliased;
		}

		public void setAliased(boolean isAliased) {
			this.isAliased = isAliased;
		}

		public String toString() {
			return className.toString();
		}

		public boolean isPredefined() {
			return isPredefined;
		}

		public void setPredefined(boolean isPredefined) {
			this.isPredefined = isPredefined;
		}

		public int getNumberOfInstantiation() {
			return numberOfInstantiation;
		}

		public void setNumberOfInstantiation(int numberOfInstantiation) {
			this.numberOfInstantiation = numberOfInstantiation;
		}

		public void incrementClassInstantiation() {
			this.numberOfInstantiation++;
		}

		public ClassDeclaration getClassDeclaration() {
			return classDeclaration;
		}

		public void setClassDeclaration(ClassDeclaration classDeclaration) {
			this.classDeclaration = classDeclaration;
		}
	}
}
