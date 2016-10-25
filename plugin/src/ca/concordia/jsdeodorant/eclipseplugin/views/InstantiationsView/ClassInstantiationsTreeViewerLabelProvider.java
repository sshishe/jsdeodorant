package ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;
import ca.concordia.jsdeodorant.eclipseplugin.util.ModulesInfo;

public class ClassInstantiationsTreeViewerLabelProvider extends StyledCellLabelProvider {
	
	private final ClassInstantiationsTreeViewerContentProvider contentProvider;

	public ClassInstantiationsTreeViewerLabelProvider(ClassInstantiationsTreeViewerContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}
	
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof String) {
			int length = contentProvider.getChildren(element).length;
			String cellText = String.format("%s (%s instance%s)", 
					element.toString().replace(ModulesInfo.getProjectRootDirectory().replace("\\", "/"), ""),
					length, length > 1 ? "s" : "");
			cell.setText(cellText);
			cell.setImage(ImagesHelper.getImageDescriptor(Constants.JS_FILE_ICON_IMAGE).createImage());
		} else if (element instanceof ObjectCreation) {
			ObjectCreation objectCreation = (ObjectCreation) element;
			cell.setText(objectCreation.toString());				
		}
		super.update(cell);
	}
	
}
