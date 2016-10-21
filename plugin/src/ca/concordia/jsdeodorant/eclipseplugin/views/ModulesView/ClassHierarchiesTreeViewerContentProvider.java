package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;

public class ClassHierarchiesTreeViewerContentProvider implements ITreeContentProvider {

	private final List<Module> modules;

	public ClassHierarchiesTreeViewerContentProvider(List<Module> modules) {
		this.modules = modules;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<ClassDeclaration> toReturn = new ArrayList<>();
		for (Module module : modules) {
			toReturn.addAll(module.getClasses());
		}
		return toReturn.toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) parentElement;
			return classDeclaration.getSubTypes().toArray();
		}
		return new Object[] {}; 
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			return classDeclaration.getSuperType();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			return classDeclaration.getSubTypes().size() > 0;
		}
		return false;
	}

}
