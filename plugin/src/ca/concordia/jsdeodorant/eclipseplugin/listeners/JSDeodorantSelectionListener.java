package ca.concordia.jsdeodorant.eclipseplugin.listeners;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.jsdt.ui.ProjectLibraryRoot;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.util.FileUtil;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;

public class JSDeodorantSelectionListener implements ISelectionListener {

	private final JSDeodorantModulesView jsModulesView;

	public JSDeodorantSelectionListener(JSDeodorantModulesView view) {
		this.jsModulesView = view;
	}

	@Override
	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			Object element = structuredSelection.getFirstElement();
			IProject selectedProject = null;
			String selectedPath = "";
			if (element instanceof IFolder) {
				IFolder iFolder = (IFolder) element;
				selectedProject = iFolder.getProject();
				selectedPath = iFolder.getRawLocation().toFile().getAbsolutePath();
			} else if (element instanceof IFile) {
				IFile iFile = (IFile) element;
				selectedProject = iFile.getProject();
				selectedPath = iFile.getRawLocation().toFile().getAbsolutePath();
			} else if (element instanceof ProjectLibraryRoot){
				ProjectLibraryRoot projectLibraryRoot = (ProjectLibraryRoot) element;
				selectedProject = projectLibraryRoot.getProject().getProject();
				selectedPath = selectedProject.getLocation().toFile().getAbsolutePath();
			} else if (element instanceof IProject) {
				selectedProject = (IProject) element;
				selectedPath = selectedProject.getLocation().toFile().getAbsolutePath();
			} else {
				selectedProject = null;
				selectedPath = "";
			}
			boolean projectIsJavaScript = false;
			if (selectedProject != null && selectedProject.isOpen()) {
				try {
					for (String projectNature : selectedProject.getDescription().getNatureIds()) {
						if (Constants.JAVASCRIPT_PROJECT_NATURE.equals(projectNature)) {
							projectIsJavaScript = true;
							break;
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (projectIsJavaScript) {
					AnalysisOptions analysisOptions = jsModulesView.getAnalysisOptions();
					File selectedPathFile = new File(selectedPath);
					analysisOptions.setDirectoryPath(selectedProject.getLocation().toFile().getAbsolutePath());
					if (selectedPathFile.isFile()) {
						analysisOptions.setJsFile(selectedPath);
					} else if (selectedPathFile.isDirectory()) {
						try {
							analysisOptions.setJsFiles(FileUtil.getFilesInDirectory(selectedPathFile.getAbsolutePath(), "js"));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				jsModulesView.setAnalysisButtonsEnabled(projectIsJavaScript);
				jsModulesView.setSelectedProjectName(selectedProject.getName());
			}
		}
	}
}
