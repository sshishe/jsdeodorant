package ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.javascript.jscomp.parsing.parser.util.SourceRange;

import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;

public class ClassInstantiationsTreeViewerContentProvider implements ITreeContentProvider {

	private final Multimap<String, ObjectCreation> fileToObjectCreationMap;
	
	public ClassInstantiationsTreeViewerContentProvider(List<ObjectCreation> objectCreationsList) {
		this.fileToObjectCreationMap = HashMultimap.create();
		for (ObjectCreation objectCreation : objectCreationsList) {
			SourceRange location = objectCreation.getNewExpressionTree().location;
			String filePath = location.start.source.name;
			fileToObjectCreationMap.put(filePath, objectCreation);
		}
	}

	@Override
	public Object[] getElements(Object arg0) {
		return fileToObjectCreationMap.keySet().toArray();
	}
	
	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof String) {
			return fileToObjectCreationMap.get((String) element).toArray();
		}
		return new Object[]{};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ObjectCreation) {
			ObjectCreation objectCreation = (ObjectCreation) element;
			return objectCreation.getNewExpressionTree().location.start.source.name;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof String) {
			return fileToObjectCreationMap.containsKey(element);
		}
		return false;
	}

}
