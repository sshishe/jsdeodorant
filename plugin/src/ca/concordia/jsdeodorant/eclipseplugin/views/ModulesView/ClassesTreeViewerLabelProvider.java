package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.File;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassMember;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;

public class ClassesTreeViewerLabelProvider extends StyledCellLabelProvider {

	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof String) {
			String string = (String) element;
			cell.setText(string);
			cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.FOLDER_ICON_IMAGE).createImage());
		} else if (element instanceof Module) {
			Module module = (Module) element;
			cell.setText((new File(module.getSourceFile().getName())).getName());
			cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.JS_FILE_ICON_IMAGE).createImage());
		} else if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			cell.setText(classDeclaration.getName());
			cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.CLASS_ICON_IMAGE).createImage());
		} else if (element instanceof ClassMember) {
			ClassMember classMember = (ClassMember) element;
			cell.setText(classMember.getName());
			if (classMember instanceof Method) {
				cell.setImage(Constants.getMethodImage((Method)classMember));
			} else {
				cell.setImage(JSDeodorantPlugin.getImageDescriptor(Constants.FIELD_ICON_IMAGE).createImage());
			}
		}
	}
}
