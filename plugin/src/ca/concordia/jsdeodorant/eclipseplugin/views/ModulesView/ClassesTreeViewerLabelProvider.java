package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.File;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo;

public class ClassesTreeViewerLabelProvider extends StyledCellLabelProvider {

	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof Module) {
			Module module = (Module) element;
			cell.setText((new File(module.getSourceFile().getName())).getName());
			cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.PACKAGE_ICON_IMAGE).createImage());
		} else if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			cell.setText(classDeclaration.getName());
			cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.CLASS_ICON_IMAGE).createImage());
		} else if (element instanceof MethodAttributeInfo) {
			MethodAttributeInfo methodAttributeInfo = (MethodAttributeInfo) element;
			cell.setText(methodAttributeInfo.getName());
			switch (methodAttributeInfo.getType()) {
			case ATTRIBUTE:
				cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.FIELD_ICON_IMAGE).createImage());
				break;
			case METHOD:
				cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.METHOD_ICON_IMAGE).createImage());
				break;
			default:
				break;
			}
		}
	}
}
