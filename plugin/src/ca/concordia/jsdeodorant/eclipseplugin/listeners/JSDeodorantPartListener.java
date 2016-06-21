package ca.concordia.jsdeodorant.eclipseplugin.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;

public class JSDeodorantPartListener implements IPartListener2 {

	private final JSDeodorantModulesView modulesView;

	public JSDeodorantPartListener(JSDeodorantModulesView view) {
		this.modulesView = view;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof IEditorPart) {
			IEditorPart iEditorPart = (IEditorPart) part;
			IAnnotationModel annotationModel = ((ITextEditor)iEditorPart).getDocumentProvider().getAnnotationModel(iEditorPart.getEditorInput());
			this.modulesView.setClearAnnotationsButtonEnabled(annotationModel.getAnnotationIterator().hasNext());
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) {}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) {}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0) {}

	@Override
	public void partOpened(IWorkbenchPartReference arg0) {}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) {}

}
