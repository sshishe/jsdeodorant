package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.module.PackageSystem;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.listeners.JSDeodorantPartListener;
import ca.concordia.jsdeodorant.eclipseplugin.listeners.JSDeodorantSelectionListener;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants.ViewID;
import ca.concordia.jsdeodorant.eclipseplugin.util.ModulesInfo;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.DependeniesView.JSDeodorantDependenciesView;
import ca.concordia.jsdeodorant.eclipseplugin.views.wizard.AnalysisOptionsWizard;
import ca.concordia.jsdeodorant.launcher.Runner;

public class JSDeodorantModulesView extends ViewPart {

	private TreeViewer classTreeViewer;

	private IAction clearAnnotationsAction;
	private IAction clearResultsAction;
	private IAction showWizardAction;
	private IAction analyzeAction;
	private IAction showDependenciesAction;
	
	private ISelectionListener selectionListener;
	private IPartListener2 partListener; 
	
	private AnalysisOptions analysisOptions;
	
	@Override
	public void createPartControl(Composite parent) {
		
		getDefaultAnalysisOptions();
		
		GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;		
	    parent.setLayout(gridLayout);
		
	    hookListeners();
	    createTreeViewer(parent);
	    createLegend(parent);
	    makeActions(parent);
	    addActionBarButtons();
	}
	
	private void getDefaultAnalysisOptions() {
		analysisOptions = new AnalysisOptions();
		analysisOptions.setPackageSystem(PackageSystem.CommonJS.name());
		analysisOptions.setModuleAnlysis(true);
		analysisOptions.setClassAnalysis(true);
		analysisOptions.setAnalyzeLibrariesForClasses(true);
	}

	private void hookListeners() {
		selectionListener = new JSDeodorantSelectionListener(analysisOptions, this);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		
		partListener = new JSDeodorantPartListener(this);
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
	}

	private void makeActions(Composite parent) {
		clearAnnotationsAction = new Action() {
			@Override
			public void run() {
				clearAnnotations();
			}
		};
		clearAnnotationsAction.setText("Clear annotations");
		clearAnnotationsAction.setToolTipText("Clear annotations");
		clearAnnotationsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
		clearAnnotationsAction.setEnabled(false);
		
		clearResultsAction = new Action() {
			@Override
			public void run() {
				clearResults();
			}
		};
		clearResultsAction.setText("Clear results");
		clearResultsAction.setToolTipText("Clear results");
		clearResultsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		clearResultsAction.setEnabled(false);
		
		showWizardAction = new Action() {
			@Override
			public void run() {
				showWizard(parent);
			}

		};
		showWizardAction.setText("Show analysis wizard");
		showWizardAction.setToolTipText("Show analysis wizard");
		showWizardAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		showWizardAction.setEnabled(false);
		
		analyzeAction = new Action() {
			@Override
			public void run() {	
				analyze();
			}
		};
		analyzeAction.setText("Start analysis");
		analyzeAction.setToolTipText("Start analysis");
		analyzeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		analyzeAction.setEnabled(false);
		
		showDependenciesAction = new Action() {
			@Override
			public void run() {
				showDependencies();
			}
		};
		showDependenciesAction.setText("Show module dependencies");
		showDependenciesAction.setToolTipText("Show module dependencies");
		showDependenciesAction.setImageDescriptor(JSDeodorantPlugin.getImageDescriptor(Constants.DEPENDENCIES_ICON_IMAGE));
		
	}

	private void addActionBarButtons() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(findDuplicatedDeclarationsAction);
//		manager.add(new Separator());
//		manager.add(clearResultsAction);
//		manager.add(clearAnnotationsAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(clearAnnotationsAction);
		manager.add(clearResultsAction);
		manager.add(showWizardAction);
		manager.add(analyzeAction);
	}

	private void createTreeViewer(Composite parent) {
		classTreeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		classTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		classTreeViewer.setContentProvider(new ClassesTreeViewerContentProvider(null));
		classTreeViewer.setLabelProvider(new ClassesTreeViewerLabelProvider());
		classTreeViewer.addDoubleClickListener(new ClassesTreeViewerDoubleClickListener());
		classTreeViewer.setInput(getViewSite());
		classTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getRightClickMenu();
			}
		});
	}
	
	private void getRightClickMenu() {
		classTreeViewer.getTree().setMenu(null);
		Module selectedModule = getSelectedModule();
		if (selectedModule != null && !selectedModule.getDependencies().isEmpty()) {
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				@Override
				public void menuAboutToShow(IMenuManager manager) { 
					manager.add(showDependenciesAction);
				}
			});
			Menu menu = menuMgr.createContextMenu(classTreeViewer.getControl());
			classTreeViewer.getControl().setMenu(menu);
			//getSite().registerContextMenu(menuMgr, classTreeViewer);
		}
	}
	
	public Module getSelectedModule() {
		ISelection selection = classTreeViewer.getSelection();
		if (!selection.isEmpty()) {
			Object firstElement = ((IStructuredSelection)selection).getFirstElement();
			if (firstElement instanceof Module)
				return (Module)firstElement;
		}
		return null;
	}

	private void createLegend(Composite parent) {
//		final Group legendGroup = new Group(parent, SWT.SHADOW_NONE);
//		legendGroup.setText("Filters");
//		GridData legendGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
//		legendGroup.setLayoutData(legendGridData);
//		GridLayout legendLayout = new GridLayout();
//		legendLayout.numColumns = 6;
//		legendLayout.horizontalSpacing = 20;
//		legendGroup.setLayout(legendLayout);
//		
//		Button filesContainingOnlyClasses = new Button(legendGroup, SWT.CHECK);
//		filesContainingOnlyClasses.setText("Only files containing class declarations");
	}

	private void analyze() {
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("JS Analysis", 1);
					Runner runner = new Runner(new String[0]) {
						@Override
						public AnalysisOptions createAnalysisOptions() {
							setAnalysisOptions(analysisOptions);
							return analysisOptions;
						}
					};
					runner.createAnalysisOptions();
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								List<Module> modules = runner.performActions();
								ModulesInfo.setModuleInfo(modules);
								classTreeViewer.setContentProvider(new ClassesTreeViewerContentProvider(modules));
								clearResultsAction.setEnabled(true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					monitor.done();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void clearResults() {
		classTreeViewer.setContentProvider(new ClassesTreeViewerContentProvider(null));
		clearResultsAction.setEnabled(false);
	}
	
	private void clearAnnotations() {
		IEditorPart activeEditor = OpenAndAnnotateHelper.getActiveEditor();
		if (activeEditor != null) {
			OpenAndAnnotateHelper.clearAnnotations(activeEditor);
		}
		clearAnnotationsAction.setEnabled(false);
	}
	
	private void showWizard(Composite parent) {
		AnalysisOptionsWizard analysisOptionsWizard = new AnalysisOptionsWizard(analysisOptions);
		WizardDialog wizardDialog = new WizardDialog(parent.getShell(), analysisOptionsWizard);
		if (wizardDialog.open() == Window.OK) {
			this.analysisOptions = analysisOptionsWizard.getAnalysisOptions();
		}
	}
	
	private void showDependencies() {
		Module selectedModule = getSelectedModule();
		List<Dependency> dependencies = selectedModule.getDependencies();
		if (!dependencies.isEmpty()) {
			IViewPart dependenciesView = OpenAndAnnotateHelper.openView(ViewID.DEPENDENCIES_VIEW);
			if (dependenciesView != null) {
				((JSDeodorantDependenciesView)dependenciesView).showDependenciesGraph(selectedModule);
			}
		}
	}
	
	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
	}

	@Override
	public void setFocus() {}

	public void setAnalysisButtonsEnabled(boolean enabled) {
		showWizardAction.setEnabled(enabled);
		analyzeAction.setEnabled(enabled);
	}

	public void setClearAnnotationsButtonEnabled(boolean enabled) {
		clearAnnotationsAction.setEnabled(enabled);		
	}
	
}
