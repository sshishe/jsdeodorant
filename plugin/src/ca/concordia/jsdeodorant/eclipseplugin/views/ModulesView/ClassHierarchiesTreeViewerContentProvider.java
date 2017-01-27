package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;

public class ClassHierarchiesTreeViewerContentProvider implements ITreeContentProvider {

	private final Set<Module> modules;

	public ClassHierarchiesTreeViewerContentProvider(Set<Module> modules) {
		this.modules = modules;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<TypeDeclaration> toReturn = new ArrayList<>();
		for (Module module : modules) {
			toReturn.addAll(module.getTypes());
		}
		return toReturn.toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TypeDeclaration) {
			TypeDeclaration classDeclaration = (TypeDeclaration) parentElement;
			return classDeclaration.getSubTypes().toArray();
		}
		return new Object[] {}; 
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TypeDeclaration) {
			TypeDeclaration classDeclaration = (TypeDeclaration) element;
			return classDeclaration.getSuperType();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TypeDeclaration) {
			TypeDeclaration classDeclaration = (TypeDeclaration) element;
			return classDeclaration.getSubTypes().size() > 0;
		}
		return false;
	}

}
