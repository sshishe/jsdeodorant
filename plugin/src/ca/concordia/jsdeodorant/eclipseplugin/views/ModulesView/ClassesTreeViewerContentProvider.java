package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo.Type;

public class ClassesTreeViewerContentProvider implements ITreeContentProvider {

	private final List<Module> modules;

	public ClassesTreeViewerContentProvider(List<Module> modules) {
		this.modules = modules;
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		if (this.modules != null) {
			return this.modules.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Module) {
			Module module = (Module) parentElement;
			return module.getClasses().toArray();
		} else if (parentElement instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) parentElement;
			
			List<MethodAttributeInfo> methodAttributeInfoList = new ArrayList<>();
			Map<String, AbstractExpression> attributes = classDeclaration.getAttributes();
			for (String attributeName : attributes.keySet()) {
				methodAttributeInfoList.add(new MethodAttributeInfo(attributeName, attributes.get(attributeName), classDeclaration, Type.ATTRIBUTE));
			}
			
			Map<String, AbstractExpression> methods = classDeclaration.getMethods();
			for (String methodName : methods.keySet()) {
				methodAttributeInfoList.add(new MethodAttributeInfo(methodName, methods.get(methodName), classDeclaration, Type.METHOD));
			}
			
			return methodAttributeInfoList.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) element;
			return classDeclaration.getParentModule();
		} else if (element instanceof MethodAttributeInfo) {
			MethodAttributeInfo methodAttributeInfo = (MethodAttributeInfo) element;
			return methodAttributeInfo.getParentClassDeclaration();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
