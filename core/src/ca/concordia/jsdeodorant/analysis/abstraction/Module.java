package ca.concordia.jsdeodorant.analysis.abstraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.runtime.directive.Foreach;

import com.google.javascript.jscomp.SourceFile;

import ca.concordia.jsdeodorant.analysis.ClassMemberCountTest;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclarationKind;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.analysis.decomposition.CompositeStatement;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.MethodType;
import ca.concordia.jsdeodorant.analysis.module.LibraryType;

public class Module {
	private List<String> messages;
	private Program program;
	private SourceFile sourceFile;
	private ModuleType moduleType;
	private LibraryType libraryType;

	@Override
	public String toString() {
		return "Module [sourceFile=" + sourceFile + ", moduleType=" + moduleType + ", libraryType=" + libraryType + "]";
	}

	private List<Dependency> dependencies;

	private List<Export> exports;
	private List<TypeDeclaration> types;

	public Module(Program program, SourceFile sourceFile, List<String> messages) {
		this.program = program;
		this.sourceFile = sourceFile;
		this.moduleType = ModuleType.File;
		this.messages = messages;
		this.dependencies = new ArrayList<>();
		this.exports = new ArrayList<>();
		this.types = new ArrayList<>();
		this.libraryType = LibraryType.NONE;
	}

	public Module(ModuleType moduleType, Program program, SourceFile sourceFile, List<String> messages) {
		this.moduleType = moduleType;
		this.program = program;
		this.sourceFile = sourceFile;
		this.messages = messages;
		this.dependencies = new ArrayList<>();
		this.exports = new ArrayList<>();
		this.types = new ArrayList<>();
		this.libraryType = LibraryType.NONE;
	}

	public List<String> getMessages() {
		return messages;
	}

	public List<TypeDeclaration> getTypes() {
		return types;
	}

	public Program getProgram() {
		return program;
	}

	public SourceFile getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	public ModuleType getPackageType() {
		return moduleType;
	}

	public void setPackageType(ModuleType packageType) {
		this.moduleType = packageType;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void addDependency(String name, Module dependentModule, AbstractExpression requireExpression) {
		Dependency dependency = new Dependency(name, requireExpression, dependentModule);
		this.dependencies.add(dependency);
		//this.dependencies.put(name, dependency);
	}

	public void addtoTypess(TypeDeclaration aTypeDeclaration) {
		for (TypeDeclaration existingClass : types) {
			if (existingClass.getFunctionDeclaration().equals(aTypeDeclaration.getFunctionDeclaration())) {
				if (!aTypeDeclaration.isInfered())
					existingClass.incrementInstantiationCount();
				return;
			}
		}
		this.types.add(aTypeDeclaration);
	}

	public List<Export> getExports() {
		return exports;
	}

	public void addExport(Export export) {
		exports.add(export);
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public void setAsLibrary(LibraryType libraryType) {
		this.libraryType = libraryType;
	}

	public String getCanonicalPath() {
		try {
			return new File(this.getSourceFile().getOriginalPath()).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TypeDeclaration createTypeDeclaration(AbstractIdentifier identifier, FunctionDeclaration functionDeclaration, 
													boolean isInfered,  boolean isAliased) {
		
		boolean hasNamespace = false;
		if (functionDeclaration instanceof FunctionDeclarationExpression)
			hasNamespace = ((FunctionDeclarationExpression) functionDeclaration).hasNamespace();

		TypeDeclaration typeDeclaration = new TypeDeclaration(functionDeclaration.getRawIdentifier(), functionDeclaration, isInfered, hasNamespace, this.getLibraryType(), isAliased,this);
		functionDeclaration.setClassDeclaration(true);
		this.addtoTypess(typeDeclaration);
		
		return typeDeclaration;
	}
	
	public void identifyConstructorInTypeDeclarationBody(){
		for(TypeDeclaration aType: this.types){
			if(!aType.hasConstructor()){
				FunctionDeclaration fn=aType.getFunctionDeclaration();
				AbstractStatement body=fn.getStatements().get(0); 
				for(AbstractStatement abstractStatement: ((CompositeStatement)body).getStatements()){
					for(FunctionDeclaration aFunctionDeclaration: abstractStatement.getFunctionDeclarationList()){
						if(!aFunctionDeclaration.isConstructor()){
							if(aFunctionDeclaration.getName().contentEquals(fn.getName())){
								aFunctionDeclaration.SetIsConstructor(true);
								aType.setHasConstrucotr(true);
								aType.getConstructors().add(aFunctionDeclaration);
							}
						}
					}
				}
			}
		}
		
	}

	public boolean isInterface(TypeDeclaration aType) {
		boolean isInterface=true;
		if(aType.isInfered()){
			for(TypeMember m: aType.getClassMembers()){
				if(m instanceof Method){ // all its method should be abstract
					if(!((Method) m).getKinds().contains(MethodType.ABSTRACT_METHOD)){
						isInterface=false;
						break;
					}
				}else{ // interface is sateless
					isInterface=false;
					break;
				}
			}
		}else{
			isInterface=false;
		}
		
		return isInterface;
	}

	public boolean isAbstractClass(TypeDeclaration aType) {
		boolean isAbstarctClass=false;
		if(aType.getKinds()!=null && aType.getKinds().contains(TypeDeclarationKind.INTERFACE)){
			return false;
		}else{
			for (TypeMember member: aType.getClassMembers()){
				if(member instanceof Method){
					if (((Method)member).getKinds().contains(MethodType.ABSTRACT_METHOD)){
						isAbstarctClass=true;
						break;
					}
				}
			}
			return isAbstarctClass;
		}
	}
}
