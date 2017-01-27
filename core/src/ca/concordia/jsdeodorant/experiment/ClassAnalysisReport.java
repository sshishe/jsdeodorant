package ca.concordia.jsdeodorant.experiment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclarationKind;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.analysis.decomposition.InferenceType;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;
import ca.concordia.jsdeodorant.analysis.util.SourceLocationHelper;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.MethodType;


public class ClassAnalysisReport {
	static Logger log = Logger.getLogger(ClassAnalysisReport.class.getName());
	private static List<ClassReportInstance> classes;

	private static void checkForInitialization() {
		if (classes == null)
			classes = new ArrayList<>();
	}

	public static void updateReport(Set<Module> modules) {
		checkForInitialization();
		for (Module module : modules) {
			for (TypeDeclaration typeDeclaration : module.getTypes()) {
				addClass(typeDeclaration, module);
			}
		}
	}

	public static void addClass(TypeDeclaration typeDeclaration, Module module) {
		ClassReportInstance classInstance = new ClassReportInstance(module, typeDeclaration);
		
		if (typeDeclaration.getRawIdentifier() != null)
			classInstance.setClassName(typeDeclaration.getRawIdentifier().toString());
		else
			classInstance.setClassName(typeDeclaration.getName());

		classInstance.setClassOffset(SourceLocationHelper.getLocation(typeDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location));
		classInstance.setConstructorLOC(typeDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location.end.line - typeDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location.start.line - 1);
		classInstance.setClassLOC(classInstance.getConstructorLOC() + typeDeclaration.getExtraMethodLines());
		classInstance.setPredefined(false);
		classInstance.setHasNewExpression(!typeDeclaration.isInfered());
		if(typeDeclaration.isInfered()){
			classInstance.setInferenceType(typeDeclaration.getInferenceType());
		}
		classInstance.setHasInfered(typeDeclaration.isInfered());
		classInstance.sethasConstructor(typeDeclaration.hasConstructor());
		classInstance.setHasNamespace(typeDeclaration.hasNamespace());
		int abstractMethods=0;
		int overridenMethods=0;
		int overridingMethods=0;
		int methodCount=0;
		int attrCount=0;
		for(TypeMember member:typeDeclaration.getTypeMembers() ){
			if(member instanceof Method ){
				methodCount++;
				if(((Method)member).getKinds().contains(MethodType.ABSTRACT_METHOD)){
					abstractMethods++;
				}
				if(((Method)member).getKinds().contains(MethodType.OVERRIDEN_METHOD)){
					overridenMethods++;
				}
				if(((Method)member).getKinds().contains(MethodType.OVERRIDING_METHOD)){
					overridingMethods++;
				}
			}else{
				attrCount++;
			}
			
		}
		classInstance.setNumberOfMethods(methodCount);
		classInstance.setNumberOfAbstractMethods(abstractMethods);
		classInstance.setNumberOfOverridenMethods(overridenMethods);
		classInstance.setNumberOfOverridingMethods(overridingMethods);
		classInstance.setNumberOfAttributes(attrCount);
		classInstance.setIsDeclarationInLibrary(typeDeclaration.getLibraryType() == LibraryType.EXTERNAL_LIBRARY);
		classInstance.setAliased(typeDeclaration.isAliased());
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
				if (classInstance.getTypeDeclaration().getFunctionDeclaration().getFunctionDeclarationTree().equals(instance.getTypeDeclaration().getFunctionDeclaration().getFunctionDeclarationTree())) {
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
		private TypeDeclaration classDeclaration;
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
		private int numberOfAbstractMethods;
		private int numberOfOverridenMethods;
		private int numberOfOverridingMethods;
		private int numberOfAttributes;
		private int numberOfParameters;
		private boolean isDeclarationInLibrary;
		private boolean isAliased;
		private int numberOfInstantiation;
		private InferenceType inferenceType;
		private boolean hasConstructor;

		public ClassReportInstance(Module module, TypeDeclaration typeDeclaration) {
			ClassAnalysisReport.checkForInitialization();
			this.classDeclaration = typeDeclaration;
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

		public boolean isInfered() {
			return hasInfered;
		}

		public void setHasInfered(boolean hasInfered) {
			this.hasInfered = hasInfered;
		}
		
		
		public boolean hasConstructor() {
			return hasConstructor;
		}

		public void sethasConstructor(boolean hasConstructor) {
			this.hasConstructor = hasConstructor;
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

		public InferenceType getInferenceType() {
			return inferenceType;
		}

		public void setInferenceType(InferenceType inferenceType) {
			this.inferenceType = inferenceType;
		}

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

		public int getNumberOfAbstractMethods() {
			return numberOfAbstractMethods;
		}

		public void setNumberOfAbstractMethods(int numberOfAbstractMethods) {
			this.numberOfAbstractMethods = numberOfAbstractMethods;
		}

		public int getNumberOfOverridenMethods() {
			return numberOfOverridenMethods;
		}

		public void setNumberOfOverridenMethods(int numberOfOverridenMethods) {
			this.numberOfOverridenMethods = numberOfOverridenMethods;
		}

		public int getNumberOfOverridingMethods() {
			return numberOfOverridingMethods;
		}

		public void setNumberOfOverridingMethods(int numberOfOverridingMethods) {
			this.numberOfOverridingMethods = numberOfOverridingMethods;
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

		public TypeDeclaration getTypeDeclaration() {
			return classDeclaration;
		}

		public void setClassDeclaration(TypeDeclaration classDeclaration) {
			this.classDeclaration = classDeclaration;
		}
	}

	public static void writeToCSV() {
		CSVFileWriter csvWriter = new CSVFileWriter("log/classes/class-declarations.csv");
		String fileHeader = "Class name, kind,file, Is Predefined, Class offset, has new expression, has inferred, Constructor Lines of codes, Total class Lines of codes, Has Namespace, Number of Methods, Number of attributes, Is Declaration in library?, is Aliased?, Number of instantiation, InferenceType, hasConstructor, numAbstractMethods, numOverridenMethods, numOverridingMethods";
		csvWriter.writeToFile(fileHeader.split(","));
		for (ClassReportInstance classReportInstance : classes) {
			StringBuilder lineToWrite = new StringBuilder();
			String kinds=getTypeDecalationKinds(classReportInstance.getTypeDeclaration());
			lineToWrite.append(classReportInstance.className).append(",").append(kinds).append(",").append(classReportInstance.getFileName()).append(",").append(classReportInstance.isPredefined()).append(",").append(classReportInstance.classOffset).append(",").append(classReportInstance.hasNewExpression).append(",").append(classReportInstance.hasInfered).append(",").append(classReportInstance.constructorLOC).append(",").append(classReportInstance.classLOC).append(",").append(classReportInstance.hasNamespace).append(",").append(classReportInstance.getNumberOfMethods()).append(",").append(classReportInstance.getNumberOfAttributes()).append(",").append(classReportInstance.isDeclarationInLibrary).append(",").append(classReportInstance.isAliased).append(",").append(classReportInstance.getNumberOfInstantiation()).append(",").append(classReportInstance.getInferenceType()).append(",").append(classReportInstance.hasConstructor()).append(",").append(classReportInstance.getNumberOfAbstractMethods()).append(",").append(classReportInstance.getNumberOfOverridenMethods()).append(",").append(classReportInstance.getNumberOfOverridingMethods());
			csvWriter.writeToFile(lineToWrite.toString().split(","));
		}

		// writing the inheritance relation
		CSVFileWriter csvWriter1 = new CSVFileWriter("log/classes/inheritance-relations.csv");
		String fileHeader1 = "Class name, kind, file,  Class offset, Super-type, Super-type-kind, Super-type1-file";
		csvWriter1.writeToFile(fileHeader1.split(","));
		for (ClassReportInstance classReportInstance : classes) {
			if(classReportInstance.getTypeDeclaration().getSuperType() !=null ){
				String kinds=getTypeDecalationKinds(classReportInstance.getTypeDeclaration());
				String SuperTypekinds=getTypeDecalationKinds(classReportInstance.getTypeDeclaration().getSuperType());
				StringBuilder lineToWrite = new StringBuilder();
				StringBuilder superTypeNames = new StringBuilder();
				superTypeNames.append(classReportInstance.getTypeDeclaration().getSuperType().getName()).append(",").append(SuperTypekinds).append(",").append(classReportInstance.getTypeDeclaration().getSuperType().getFunctionDeclaration().getFunctionDeclarationTree().location.start.source.name).append(",");
				lineToWrite.append(classReportInstance.className).append(",").append(kinds).append(",").append(classReportInstance.getFileName()).append(",").append(classReportInstance.classOffset).append(",").append(superTypeNames);
				csvWriter1.writeToFile(lineToWrite.toString().split(","));
			}
			
		}
		
	}
	
	private static String getTypeDecalationKinds(TypeDeclaration aTypeDeclaration){
		String kinds="";
		if(aTypeDeclaration.getKinds()==null){
			kinds="DONT KNOW";
		}
		else if(aTypeDeclaration.getKinds().size()==1){
			kinds=aTypeDeclaration.getKinds().iterator().next().toString();
		}else{
			for(TypeDeclarationKind kind: aTypeDeclaration.getKinds()){
				kinds= kinds+ "-"+kind;
			}
			kinds=kinds.substring(1);
		}
		return kinds;
	}
}

