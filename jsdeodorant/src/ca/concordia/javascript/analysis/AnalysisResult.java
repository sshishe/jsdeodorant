package ca.concordia.javascript.analysis;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.Module;

public final class AnalysisResult {
	private static List<Module> packageInstances = new ArrayList<>();
	private static int totalNumberOfClasses = 0;
	private static int totalNumberOfFiles = 0;

	private AnalysisResult() {

	}

	public static int getTotalNumberOfClasses() {
		return totalNumberOfClasses;
	}

	public static void increaseTotalNumberOfClasses(int increment) {
		AnalysisResult.totalNumberOfClasses += increment;
	}

	public static void addPackageInstance(Module instance) {
		packageInstances.add(instance);
	}

	public static List<Module> getPackageInstance() {
		return packageInstances;
	}

	public static int getTotalNumberOfFiles() {
		return totalNumberOfFiles;
	}

	public static void increaseTotalNumberOfFiles() {
		AnalysisResult.totalNumberOfFiles++;
	}
}
