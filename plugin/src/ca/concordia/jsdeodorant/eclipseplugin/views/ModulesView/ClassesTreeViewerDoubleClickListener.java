package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotation;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ClassesTreeViewerDoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		
		IStructuredSelection selection = (IStructuredSelection)event.getViewer().getSelection();
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof Module) {
			Module module = (Module) firstElement;
			OpenAndAnnotateHelper.openEditorAndAnnotate(module.getSourceFile().getOriginalPath(), new JSAnnotation[] {});
		} else if (firstElement instanceof TypeDeclaration) {
			TypeDeclaration classDeclaration = (TypeDeclaration) firstElement;
			OpenAndAnnotateHelper.openAndAnnotateClassDeclaration(classDeclaration);
		} else if (firstElement instanceof TypeMember) {
			TypeMember classMember = (TypeMember) firstElement;
			OpenAndAnnotateHelper.openAndAnnotateMethodOrAttribute(classMember);
		}
	}

}
