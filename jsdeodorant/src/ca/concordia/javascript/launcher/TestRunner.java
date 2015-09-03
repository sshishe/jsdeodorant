package ca.concordia.javascript.launcher;

import java.util.ArrayList;

import ca.concordia.javascript.analysis.AnalysisOptions;

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
		getAnalysisOptions().setAdvancedAnalysis(state);
	}

	public void setJsFile(String jsFilej) {
		getAnalysisOptions().setJsFile(jsFilej);
	}

	@Override
	public AnalysisOptions createAnalysisOptions() {
		setAnalysisOptions(new AnalysisOptions());
		getAnalysisOptions().setAdvancedAnalysis(true);
		getAnalysisOptions().setCalculateCyclomatic(false);
		getAnalysisOptions().setOutputToCSV(false);
		getAnalysisOptions().setLogDisabled(true);
		return getAnalysisOptions();
	}
}
