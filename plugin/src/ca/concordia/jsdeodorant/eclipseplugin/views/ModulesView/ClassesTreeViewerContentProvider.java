package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassMember;

public class ClassesTreeViewerContentProvider implements ITreeContentProvider {

	private final List<Module> modules;
	private final Multimap<String, Module> parentFolders;
	private final String rootDirectory;
	
	public ClassesTreeViewerContentProvider(List<Module> modules, String rootDirectory) {
		this.modules = modules;
		this.rootDirectory = rootDirectory;
		this.parentFolders = ArrayListMultimap.create();
		if (modules != null) {
			for (Module module : modules) {
				String parentFolderName = getModuleParentName(module);
				this.parentFolders.put(parentFolderName, module);
			}
		}
	}

	private String getModuleParentName(Module module) {
		File parentFile = new File(module.getSourceFile().getOriginalPath()).getParentFile();
		String parentFolderName = "";
		if (parentFile != null) {
			try {
				parentFolderName = parentFile.getCanonicalFile().getAbsolutePath().replace(rootDirectory, "").replace("\\", "/");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("".equals(parentFolderName.trim())) {
			parentFolderName = "/";
		}
		return parentFolderName;
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
			return getModuleParentName(module);
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
