package ca.concordia.jsdeodorant.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.util.FileUtil;

public class TestRunner extends Runner {
	public TestRunner() {
		this(new String[0]);
	}

	protected TestRunner(String[] args) {
		super(args);
		createOptions();
		createAnalysisOptions();
		getAnalysisOptions().setExterns(new ArrayList<String>());
	}

	public void setAdvancedAnalysis(boolean state) {
		getAnalysisOptions().setClassAnalysis(state);
	}

	public void setJsFile(String jsFile) {
		getAnalysisOptions().setJsFile(jsFile);
	}

	public void setDirectoryPath(String jsDirectoryPath) {
		try {
			getAnalysisOptions().setJsFiles(FileUtil.getFilesInDirectory(jsDirectoryPath, "js"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public AnalysisOptions createAnalysisOptions() {
		setAnalysisOptions(new AnalysisOptions());
		getAnalysisOptions().setModuleAnlysis(true);
		getAnalysisOptions().setClassAnalysis(true);
		getAnalysisOptions().setCalculateCyclomatic(false);
		getAnalysisOptions().setOutputToCSV(false);
		getAnalysisOptions().setLogDisabled(true);
		getAnalysisOptions().setOutputToDB(false);
		getAnalysisOptions().setClassAnalysis(true);
		getAnalysisOptions().setModuleAnlysis(true);
		getAnalysisOptions().setFunctionAnalysis(true);
		getAnalysisOptions().setAnalyzeLibrariesForClasses(true);
		getAnalysisOptions().setPackageSystem("CommonJS");
		return getAnalysisOptions();
	}

	public Module performActionsForModule() {
		try {
			return super.performActions().get(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Module> performActionsForModules() {
		try {
			return super.performActions();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
