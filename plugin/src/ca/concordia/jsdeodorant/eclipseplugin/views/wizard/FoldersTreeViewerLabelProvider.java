package ca.concordia.jsdeodorant.eclipseplugin.views.wizard;

import java.io.File;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class FoldersTreeViewerLabelProvider extends StyledCellLabelProvider {

	private static final Image FOLDER_ICON_IMAGE = 
			PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER).createImage();
	
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof File) {
			File file = (File) element;
			cell.setText(file.getName());
			cell.setImage(FOLDER_ICON_IMAGE);
		}
	}
	
}
