package ca.concordia.javascript.analysis;

import java.util.List;

import com.google.common.collect.Lists;

public class AnalysisOptions {
	private boolean advancedAnalysis;
	private boolean calculateCyclomatic;
	private boolean outputToCSV;
	private boolean logDisabled;
	private String directoryPath;
	private List<String> jsFiles;
	private List<String> externs;

	public boolean isAdvancedAnalysis() {
		return advancedAnalysis;
	}

	public void setAdvancedAnalysis(boolean advancedAnalysis) {
		this.advancedAnalysis = advancedAnalysis;
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
}