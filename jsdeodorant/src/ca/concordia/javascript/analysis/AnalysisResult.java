package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
	private static List<AnalysisInstance> analysisInstances;
	private static int totalNumberOfClasses = 0;

	public AnalysisResult() {
		if (analysisInstances == null)
			analysisInstances = new ArrayList<>();
	}

	public static int getTotalNumberOfClasses() {
		return totalNumberOfClasses;
	}

	public static void setTotalNumberOfClasses(int totalNumberOfClasses) {
		AnalysisResult.totalNumberOfClasses = totalNumberOfClasses;
	}
}
