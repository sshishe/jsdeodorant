package ca.concordia.jsdeodorant.eclipseplugin.views.wizard;

import org.eclipse.jface.wizard.Wizard;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.ClassAnalysisMode;

public class AnalysisOptionsWizard extends Wizard {
	
	private AnalysisOptions analysisOptions;
	private AnalysisOptionsWizardPage analysisOptionsPage;
	
	public AnalysisOptionsWizard(AnalysisOptions analysisOptions) {
		this.analysisOptions = analysisOptions;
	}
	
	@Override
	public void addPages() {
		analysisOptionsPage = new AnalysisOptionsWizardPage(analysisOptions);
		addPage(analysisOptionsPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public AnalysisOptions getAnalysisOptions() {
		AnalysisOptions analysisOptions = new AnalysisOptions();
		analysisOptions.setPackageSystem(analysisOptionsPage.getPackageSystem().name());
		analysisOptions.setModuleAnlysis(analysisOptionsPage.isModuleAnlysisChecked());
		analysisOptions.setClassAnalysis(analysisOptionsPage.isClassAnalysisChecked());
		analysisOptions.setFunctionAnalysis(analysisOptionsPage.isFunctionAnalysisChecked());
		analysisOptions.setAnalyzeLibrariesForClasses(analysisOptionsPage.isAnalyzeLibrariesForClassesChecked());
		analysisOptions.setDirectoryPath(analysisOptionsPage.getDirectoryPath());
		analysisOptions.setLibrariesWithPath(analysisOptionsPage.getLibraryPaths());
		analysisOptions.setJsFiles(analysisOptionsPage.getJsFiles());
		analysisOptions.setClassAnalysisMode(ClassAnalysisMode.STRICT.toString());
		return analysisOptions;
	}

}
