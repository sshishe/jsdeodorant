package ca.concordia.jsdeodorant.eclipseplugin.views.wizard;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FoldersTreeViewerContentProvider implements ITreeContentProvider {
	
	private File[] folders;
	
	private FileFilter folderFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	public FoldersTreeViewerContentProvider(String pathToJSPorjectFolder) {
		File rootFolder = new File(pathToJSPorjectFolder);
		folders = rootFolder.listFiles(folderFilter);
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

	@Override
	public Object[] getElements(Object inputElement) {
		return folders;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof File) {
			File folder = (File) parentElement;
			return folder.listFiles(folderFilter);
		} else {
			return new File[] {};
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof File) {
			File file = (File) element;
			return file.getParentFile();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof File) {
			File file = (File) element;
			return file.listFiles(folderFilter).length > 0;
		}
		return false;
	}

}
