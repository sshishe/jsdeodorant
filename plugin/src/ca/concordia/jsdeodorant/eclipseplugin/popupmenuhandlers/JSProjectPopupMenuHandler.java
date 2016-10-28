package ca.concordia.jsdeodorant.eclipseplugin.popupmenuhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;

public class JSProjectPopupMenuHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			ISelection selection = activeWorkbenchWindow.getSelectionService().getSelection();
			JSDeodorantModulesView modulesView = (JSDeodorantModulesView)OpenAndAnnotateHelper.openView(JSDeodorantModulesView.ID);
			modulesView.getSelectedProject(selection);
		}
		return null;
	}

}
