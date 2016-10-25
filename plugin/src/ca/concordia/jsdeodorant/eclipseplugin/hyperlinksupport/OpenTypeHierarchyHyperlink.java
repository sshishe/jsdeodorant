package ca.concordia.jsdeodorant.eclipseplugin.hyperlinksupport;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;

public class OpenTypeHierarchyHyperlink implements IHyperlink {

	private final IRegion region;
	private final ClassDeclaration classDeclaration;

	public OpenTypeHierarchyHyperlink(IRegion region, ClassDeclaration classDeclaration) {
		this.region = region;
		this.classDeclaration = classDeclaration;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	@Override
	public String getHyperlinkText() {
		if (this.classDeclaration != null) {
			return "JSDeodorant: Open class hierarchy";
		}
		return null;
	}

	@Override
	public String getTypeLabel() {
		if (this.classDeclaration != null) {
			return "JSDeodorant: Open hierarchy";
		}
		return null;
	}

	@Override
	public void open() {
		if (classDeclaration != null) {
			JSDeodorantModulesView modulesView = ((JSDeodorantModulesView)OpenAndAnnotateHelper.openView(JSDeodorantModulesView.ID));
			modulesView.showTypeHierarchyForClassDeclaration(classDeclaration);
		}
	}

}
