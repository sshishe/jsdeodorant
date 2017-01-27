package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.util.ModulesInfo;

public class ClassesTreeViewerContentProvider implements ITreeContentProvider {

	private final Set<Module> modules;
	private final Multimap<String, Module> parentFolders;
	
	public ClassesTreeViewerContentProvider(Set<Module> modules) {
		this.modules = modules;
		this.parentFolders = ArrayListMultimap.create();
		if (modules != null) {
			for (Module module : modules) {
				String parentFolderName = ModulesInfo.getModuleParentName(module);
				this.parentFolders.put(parentFolderName, module);
			}
		}
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		if (modules != null) {
			return parentFolders.keySet().toArray(); 
		}
		return new Object[] {};
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			String parentPath = (String) parentElement;
			return parentFolders.get(parentPath).toArray();
		} else if (parentElement instanceof Module) {
			Module module = (Module) parentElement;
			return module.getTypes().toArray();
		} else if (parentElement instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) parentElement;
			return typeDeclaration.getTypeMembers().toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Module) {
			Module module = (Module) element;
			return ModulesInfo.getModuleParentName(module);
		} else if (element instanceof TypeDeclaration) {
			TypeDeclaration classDeclaration = (TypeDeclaration) element;
			return classDeclaration.getParentModule();
		} else if (element instanceof TypeMember) {
			TypeMember classMember = (TypeMember) element;
			return classMember.getOwner();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
