package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;

public final class AnalysisResult {
	private static List<AnalysisInstance> analysisInstances = new ArrayList<>();
	private static int totalNumberOfClasses = 0;

	private AnalysisResult() {

	}

	public static int getTotalNumberOfClasses() {
		return totalNumberOfClasses;
	}

	public static void setTotalNumberOfClasses(int totalNumberOfClasses) {
		AnalysisResult.totalNumberOfClasses = totalNumberOfClasses;
	}

	public static void addAnalysisInstance(AnalysisInstance instance) {
		analysisInstances.add(instance);
	}

	public static List<AnalysisInstance> getAnalysisInstances() {
		return analysisInstances;
	}
}
