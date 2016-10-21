package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassMember;

public class ClassesTreeViewerContentProvider implements ITreeContentProvider {

	private final List<Module> modules;

	public ClassesTreeViewerContentProvider(List<Module> modules) {
		this.modules = modules;
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		if (this.modules != null) {
			return this.modules.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Module) {
			Module module = (Module) parentElement;
			return module.getClasses().toArray();
		} else if (parentElement instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) parentElement;
			return classDeclaration.getClassMembers().toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			return classDeclaration.getParentModule();
		} else if (element instanceof ClassMember) {
			ClassMember classMember = (ClassMember) element;
			return classMember.getOwner();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
