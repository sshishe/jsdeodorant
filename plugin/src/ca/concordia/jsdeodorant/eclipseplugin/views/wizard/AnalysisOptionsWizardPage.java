package ca.concordia.jsdeodorant.eclipseplugin.views.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.module.PackageSystem;

public class AnalysisOptionsWizardPage extends WizardPage {
	
	private boolean isClassAnalysisChecked;
	private boolean isFunctionAnalysisChecked;
	private boolean isModuleAnlysisChecked;
	private boolean isAnalyzeLibrariesForClassesChecked;
	private PackageSystem packageSystem;
	private Set<String> libraryPaths;
	private final String directoryPath;
	private final List<String> jsFiles;
	
	protected AnalysisOptionsWizardPage(AnalysisOptions analysisOptions) {
		super("Analysis Options");
		setDescription("Set the analysis options");
		this.isClassAnalysisChecked = analysisOptions.hasClassAnlysis();
		this.isFunctionAnalysisChecked = analysisOptions.hasFunctionAnlysis();
		this.isModuleAnlysisChecked = analysisOptions.hasModuleAnalysis();
		this.isAnalyzeLibrariesForClassesChecked = analysisOptions.analyzeLibrariesForClasses();
		this.packageSystem = analysisOptions.getPackageSystem();
		this.jsFiles = analysisOptions.getJsFiles();
		List<String> libraries = analysisOptions.getLibrariesWithPath();
		if (libraries != null) {
			this.libraryPaths = new LinkedHashSet<>(libraries);
		} else {
			this.libraryPaths = new LinkedHashSet<>();
		}
		this.directoryPath = analysisOptions.getDirectoryPath();
	}

	@Override
	public void createControl(Composite parent) {
		
		GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;		
	    parent.setLayout(gridLayout);
	    
		GridData groupGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		
		Group analysisGroup = new Group(parent, SWT.SHADOW_NONE);
		analysisGroup.setText("Analysis");
		analysisGroup.setLayoutData(groupGridData);
		analysisGroup.setLayout(groupLayout);
		
		Button classAnalysisButton = new Button(analysisGroup, SWT.CHECK);
		classAnalysisButton.setText("Class analysis");
		classAnalysisButton.setSelection(isClassAnalysisChecked);
		classAnalysisButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isClassAnalysisChecked = classAnalysisButton.getSelection();
			}
		});
		
//		Button functionAnalysisButton = new Button(analysisGroup, SWT.CHECK);
//		functionAnalysisButton.setText("Function analysis");
//		functionAnalysisButton.setSelection(isFunctionAnalysisChecked);
//		functionAnalysisButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				isFunctionAnalysisChecked = functionAnalysisButton.getSelection();
//			}
//		});
		
		Group libraryAnalysisGroup = new Group(parent, SWT.SHADOW_NONE);
		libraryAnalysisGroup.setText("Library analysis");
		libraryAnalysisGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		libraryAnalysisGroup.setLayout(groupLayout);
		
		Button analyzeLibrariesForClassesButton = new Button(libraryAnalysisGroup, SWT.CHECK);
		analyzeLibrariesForClassesButton.setText("Analyze library classes");
		analyzeLibrariesForClassesButton.setSelection(isAnalyzeLibrariesForClassesChecked);
		
		CheckboxTreeViewer libraryFoldersTree = new CheckboxTreeViewer(libraryAnalysisGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		libraryFoldersTree.setContentProvider(new FoldersTreeViewerContentProvider(directoryPath));
		libraryFoldersTree.setLabelProvider(new FoldersTreeViewerLabelProvider());
		libraryFoldersTree.setCheckStateProvider(new FoldersTreeViewerCheckStateProvider(libraryPaths));
		libraryFoldersTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		libraryFoldersTree.getTree().setEnabled(isAnalyzeLibrariesForClassesChecked);
		libraryFoldersTree.setInput("");
		libraryFoldersTree.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if (element instanceof File) {
					File file = (File) element;
					libraryPaths.clear();
					libraryFoldersTree.setSubtreeChecked(file, event.getChecked());
					for (Object object : libraryFoldersTree.getCheckedElements()) {
						libraryPaths.add(((File)object).getAbsolutePath());	
					}
				};
			}
		});
		
		analyzeLibrariesForClassesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isAnalyzeLibrariesForClassesChecked = analyzeLibrariesForClassesButton.getSelection();
				libraryFoldersTree.getTree().setEnabled(isAnalyzeLibrariesForClassesChecked);
			}
		});
		
		Group moduleAnalysisGroup = new Group(parent, SWT.SHADOW_NONE);
		moduleAnalysisGroup.setText("Module analysis");
		moduleAnalysisGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		moduleAnalysisGroup.setLayout(new GridLayout(3, false));
		
		Button analyzeModulesButton = new Button(moduleAnalysisGroup, SWT.CHECK);
		analyzeModulesButton.setText("Analyze modules");
		analyzeModulesButton.setSelection(isModuleAnlysisChecked);
		
		Label packageSystemLabel = new Label(moduleAnalysisGroup, SWT.NONE);
		packageSystemLabel.setText("Package system:");
		Combo packageSystemCombo = new Combo(moduleAnalysisGroup, SWT.READ_ONLY);
		for (PackageSystem packageSystem : PackageSystem.values()) {
			packageSystemCombo.add(packageSystem.name());
		}
		packageSystemCombo.setEnabled(isModuleAnlysisChecked);
		if (packageSystem != null) {
			packageSystemCombo.select(packageSystem.ordinal());
		}
		packageSystemCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				packageSystem = PackageSystem.valueOf(packageSystemCombo.getText());
			}
		});
		
		analyzeModulesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isModuleAnlysisChecked = analyzeModulesButton.getSelection();
				packageSystemCombo.setEnabled(analyzeModulesButton.getSelection());
			}
		});
		
		setControl(analysisGroup);
		
	}

	public boolean isClassAnalysisChecked() {
		return isClassAnalysisChecked;
	}

	public boolean isFunctionAnalysisChecked() {
		return isFunctionAnalysisChecked;
	}

	public PackageSystem getPackageSystem() {
		return packageSystem;
	}

	public boolean isAnalyzeLibrariesForClassesChecked() {
		return isAnalyzeLibrariesForClassesChecked;
	}
	
	public boolean isModuleAnlysisChecked() {
		return isModuleAnlysisChecked;
	}

	public List<String> getLibraryPaths() {
		return new ArrayList<>(libraryPaths);
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public List<String> getJsFiles() {
		return jsFiles;
	}

}
