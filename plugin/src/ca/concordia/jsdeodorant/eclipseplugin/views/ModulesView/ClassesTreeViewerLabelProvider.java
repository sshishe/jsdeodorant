package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.File;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;

public class ClassesTreeViewerLabelProvider extends StyledCellLabelProvider {

	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof String) {
			String string = (String) element;
			cell.setText(string);
			cell.setImage(ImagesHelper.getImageDescriptor(Constants.FOLDER_ICON_IMAGE).createImage());
		} else if (element instanceof Module) {
			Module module = (Module) element;
			cell.setText((new File(module.getSourceFile().getName())).getName());
			cell.setImage(ImagesHelper.getImageDescriptor(Constants.JS_FILE_ICON_IMAGE).createImage());
		} else if (element instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) element;
			cell.setText(typeDeclaration.getName());
			cell.setImage(ImagesHelper.getTypeImage(typeDeclaration));
		} else if (element instanceof TypeMember) {
			TypeMember classMember = (TypeMember) element;
			cell.setText(classMember.getName());
			if (classMember instanceof Method) {
				cell.setImage(ImagesHelper.getMethodImage((Method)classMember));
			} else {
				cell.setImage(ImagesHelper.getImageDescriptor(Constants.FIELD_ICON_IMAGE).createImage());
			}
		}
	}
}
