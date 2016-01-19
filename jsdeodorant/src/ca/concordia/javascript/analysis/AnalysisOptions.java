package ca.concordia.javascript.analysis;

import java.util.List;

import com.google.common.collect.Lists;

public class AnalysisOptions {
	private boolean classAnalysis;
	private boolean moduleAnalysis;
	private boolean calculateCyclomatic;
	private boolean outputToCSV;
	private boolean outputToDB;
	private boolean logDisabled;
	private String directoryPath;
	private List<String> jsFiles;
	private List<String> externs;
	private List<String> libraries;
	private boolean analyzeLibrariesForClasses;
	private List<String> librariesWithPath;
	private List<String> builtInLibraries;
	private String psqlServerName;
	private String psqlPortNumber;
	private String psqlDatabase;
	private String psqlUser;
	private String psqlPassword;

	public boolean hasClassAnlysis() {
		return classAnalysis;
	}

	public void setClassAnalysis(boolean classAnlysis) {
		this.classAnalysis = classAnlysis;
	}

	public boolean hasModuleAnalysis() {
		return moduleAnalysis;
	}

	public void setModuleAnlysis(boolean moduleAnalysis) {
		this.moduleAnalysis = moduleAnalysis;
	}

	public boolean isCalculateCyclomatic() {
		return calculateCyclomatic;
	}

	public void setCalculateCyclomatic(boolean calculateCyclomatic) {
		this.calculateCyclomatic = calculateCyclomatic;
	}

	public boolean isOutputToCSV() {
		return outputToCSV;
	}

	public void setOutputToCSV(boolean outputToCSV) {
		this.outputToCSV = outputToCSV;
	}

	public boolean isOutputToDB() {
		return outputToDB;
	}

	public void setOutputToDB(boolean outputToDB) {
		this.outputToDB = outputToDB;
	}

	public boolean isLogDisabled() {
		return logDisabled;
	}

	public void setLogDisabled(boolean disableLog) {
		this.logDisabled = disableLog;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public List<String> getJsFiles() {
		return jsFiles;
	}

	public void setJsFiles(List<String> jsFiles) {
		this.jsFiles = jsFiles;
	}

	public void setJsFile(String jsFile) {
		this.jsFiles = Lists.newArrayList(jsFile);
	}

	public List<String> getExterns() {
		return externs;
	}

	public void setExterns(List<String> externs) {
		this.externs = externs;
	}

	public List<String> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<String> libraries) {
		this.libraries = libraries;
	}

	public boolean analyzeLibrariesForClasses() {
		return analyzeLibrariesForClasses;
	}

	public void setAnalyzeLibrariesForClasses(boolean flag) {
		this.analyzeLibrariesForClasses = flag;
	}

	public List<String> getLibrariesWithPath() {
		return librariesWithPath;
	}

	public void setLibrariesWithPath(List<String> libraries) {
		this.librariesWithPath = libraries;
	}

	public List<String> getBuiltInLibraries() {
		return builtInLibraries;
	}

	public void setBuiltinLibraries(List<String> builtInLibraries) {
		this.builtInLibraries = builtInLibraries;
	}

	public String getPsqlServerName() {
		return psqlServerName;
	}

	public void setPsqlServerName(String serverName) {
		this.psqlServerName = serverName;
	}

	public String getPsqlPortNumber() {
		return psqlPortNumber;
	}

	public void setPsqlPortNumber(String portNumber) {
		this.psqlPortNumber = portNumber;
	}

	public String getPsqlDatabaseName() {
		return psqlDatabase;
	}

	public void setPsqlDatabaseName(String database) {
		this.psqlDatabase = database;
	}

	public String getPsqlUser() {
		return psqlUser;
	}

	public void setPsqlUser(String user) {
		this.psqlUser = user;
	}

	public String getPsqlPassword() {
		return psqlPassword;
	}

	public void setPsqlPassword(String password) {
		this.psqlPassword = password;
	}
}
