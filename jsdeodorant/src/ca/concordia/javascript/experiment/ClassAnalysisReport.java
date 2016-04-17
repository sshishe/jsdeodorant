package ca.concordia.javascript.experiment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.decomposition.ClassDeclaration;
import ca.concordia.javascript.analysis.module.LibraryType;
import ca.concordia.javascript.analysis.util.SourceLocationHelper;

public class ClassAnalysisReport {
	static Logger log = Logger.getLogger(ClassAnalysisReport.class.getName());
	private static List<ClassReportInstance> classes;

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
		ClassReportInstance classInstance = new ClassReportInstance(module, classDeclaration);
		classInstance.setClassName(classDeclaration.getName());
		classInstance.setClassOffset(SourceLocationHelper.getLocation(classDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location));
		classInstance.setConstructorLOC(classDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location.end.line - classDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location.start.line - 1);
		classInstance.setClassLOC(classInstance.getConstructorLOC() + classDeclaration.getExtraMethodLines());
		classInstance.setPredefined(false);
		classInstance.setHasNewExpression(!classDeclaration.isInfered());
		classInstance.setHasInfered(classDeclaration.isInfered());
		classInstance.setHasNamespace(classDeclaration.hasNamespace());
		classInstance.setNumberOfMethods(classDeclaration.getMethods().size());
		classInstance.setNumberOfAttributes(classDeclaration.getAttributes().size());
		classInstance.setIsDeclarationInLibrary(classDeclaration.getLibraryType() == LibraryType.EXTERNAL_LIBRARY);
		classInstance.setAliased(classDeclaration.isAliased());
		add(classInstance);
	}

	public static void addPredefinedClass(ObjectCreation creation, Module module) {
		ClassReportInstance classInstance = new ClassReportInstance(module, creation.getClassDeclaration());
		classInstance.setClassName(creation.getIdentifier().toString());
		classInstance.setPredefined(true);
		//		classInstance.setNewExpressionFile(module.getCanonicalPath());
		//		classInstance.setNewExpressionOffset(SourceLocationHelper.getLocation(creation.getOperandOfNew().getExpression().location));
		classInstance.setHasNewExpression(true);
		classInstance.setHasInfered(false);
		classInstance.setHasNamespace(false);
		add(classInstance);
	}

	public static void add(ClassReportInstance instance) {
		for (ClassReportInstance classInstance : classes) {
			if (!instance.isPredefined() && !classInstance.isPredefined()) {
				if (classInstance.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree().equals(instance.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree())) {
					if (classInstance.hasNewExpression)
						classInstance.incrementClassInstantiation();
					return;
				}
			} else {
				// it's predefined
			}
		}
		classes.add(instance);
	}

	public static List<ClassReportInstance> getList() {
		return classes;
	}

	public static int getClassCount() {
		return classes.size();
	}

	public static ClassReportInstance get(String className, String file) {
		for (ClassReportInstance classInstance : classes) {
			if (classInstance.getClassName().equals(className) && classInstance.getFileName().equals(file))
				return classInstance;
		}
		return null;
	}

	public static class ClassReportInstance {
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
		private int numberOfMethods;
		private int numberOfAttributes;
		private int numberOfParameters;
		private boolean isDeclarationInLibrary;
		private boolean isAliased;
		private int numberOfInstantiation;

		public ClassReportInstance(Module module, ClassDeclaration classDeclaration) {
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
			this.classOffset = classOffset.replace(",", "-");
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

		public void setConstructorLOC(int addMethod) {
			if (addMethod < 0)
				addMethod = 0;
			this.constructorLOC = addMethod;
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

		public boolean getIsDeclarationInLibrary() {
			return isDeclarationInLibrary;
		}

		public void setIsDeclarationInLibrary(boolean isDeclarationInLibrary) {
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

	public static void writeToCSV() {
		CSVFileWriter csvWriter = new CSVFileWriter("log/classes/class-declarations.csv");
		String fileHeader = "Class name, file, Is Predefined, Class offset, has new expression, has inferred, Constructor Lines of codes, Total class Lines of codes, Has Namespace, Number of Methods, Number of attributes, Is Declaration in library?, is Aliased?, Number of instantiation";
		csvWriter.writeToFile(fileHeader.split(","));
		for (ClassReportInstance classReportInstance : classes) {
			StringBuilder lineToWrite = new StringBuilder();
			lineToWrite.append(classReportInstance.className).append(",").append(classReportInstance.getFileName()).append(",").append(classReportInstance.isPredefined()).append(",").append(classReportInstance.classOffset).append(",").append(classReportInstance.hasNewExpression).append(",").append(classReportInstance.hasInfered).append(",").append(classReportInstance.constructorLOC).append(",").append(classReportInstance.classLOC).append(",").append(classReportInstance.hasNamespace).append(",").append(classReportInstance.getNumberOfMethods()).append(",").append(classReportInstance.getNumberOfAttributes()).append(",").append(classReportInstance.isDeclarationInLibrary).append(",").append(classReportInstance.isAliased).append(",").append(classReportInstance.getNumberOfInstantiation());
			csvWriter.writeToFile(lineToWrite.toString().split(","));
		}

	}
}
