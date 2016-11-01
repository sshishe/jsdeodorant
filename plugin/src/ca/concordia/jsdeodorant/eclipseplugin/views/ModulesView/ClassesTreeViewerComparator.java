package ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;

public class ClassesTreeViewerComparator extends ViewerComparator {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		String name1 = "", name2 = "";

		if (e1.getClass() == e2.getClass()) {
			if (e1 instanceof String) {
				name1 = e1.toString();
				name2 = e2.toString();
			} else if (e1 instanceof Module) {
				name1 = (new File(((Module) e1).getSourceFile().getName())).getName();
				name2 = (new File(((Module) e2).getSourceFile().getName())).getName();
			} else if (e1 instanceof TypeDeclaration) {
				name1 = ((TypeDeclaration) e1).getName();
				name2 = ((TypeDeclaration) e2).getName();
			} else if (e1 instanceof TypeMember) {
				name1 = ((TypeMember) e1).getName();
				name2 = ((TypeMember) e2).getName();
			}
		} else {
			name1 = e1.toString();
			name2 = e2.toString();
		}

		return name1.compareToIgnoreCase(name2);
	}
}
