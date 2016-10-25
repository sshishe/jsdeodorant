package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassMember;
import ca.concordia.jsdeodorant.eclipseplugin.util.ModulesInfo;

public class ClassesTreeViewerContentProvider implements ITreeContentProvider {

	private final List<Module> modules;
	private final Multimap<String, Module> parentFolders;
	
	public ClassesTreeViewerContentProvider(List<Module> modules) {
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
			return module.getClasses().toArray();
		} else if (parentElement instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) parentElement;
			return classDeclaration.getClassMembers().toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Module) {
			Module module = (Module) element;
			return ModulesInfo.getModuleParentName(module);
		} else if (element instanceof ClassDeclaration) {
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
