package ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotation;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ClassInstantiationsTreeViewerDoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getViewer().getSelection();
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof String) {
			OpenAndAnnotateHelper.openEditorAndAnnotate(firstElement.toString(), new JSAnnotation[]{});
		} if (firstElement instanceof ObjectCreation) {
			ObjectCreation objectCreation = (ObjectCreation) firstElement;
			OpenAndAnnotateHelper.openAndAnnotateObjectCreation(objectCreation);
		}
	}

}
