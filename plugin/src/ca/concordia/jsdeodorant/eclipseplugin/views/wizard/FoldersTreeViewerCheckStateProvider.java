package ca.concordia.jsdeodorant.eclipseplugin.views.wizard;

import java.io.File;
import java.util.Collection;

import org.eclipse.jface.viewers.ICheckStateProvider;

public class FoldersTreeViewerCheckStateProvider implements ICheckStateProvider {
	
	private final Collection<String> libraryPaths;

	public FoldersTreeViewerCheckStateProvider(Collection<String> libraryPaths) {
		this.libraryPaths = libraryPaths;
	}

	@Override
	public boolean isChecked(Object element) {
		if (element instanceof File) {
			File file = (File) element;
			return libraryPaths.contains(file.getAbsolutePath());
		}
		return false;
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}

}
