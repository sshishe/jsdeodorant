package ca.concordia.jsdeodorant.eclipseplugin.hyperlinksupport;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView.JSDeodorantClassInstantiationsView;


public class ClassInstantiationsHyperlink implements IHyperlink {

	private final IRegion region;
	private final TypeDeclaration typeDeclaration;

	public ClassInstantiationsHyperlink(IRegion region, TypeDeclaration typeDeclaration) {
		this.region = region;
		this.typeDeclaration = typeDeclaration;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	@Override
	public String getHyperlinkText() {
		if (this.typeDeclaration != null) {
			return "JSDeodorant: Find instantiations";
		}
		return null;
	}

	@Override
	public String getTypeLabel() {
		if (this.typeDeclaration != null) {
			return "JSDeodorant: Find instantiations";
		}
		return null;
	}

	@Override
	public void open() {
		if (typeDeclaration != null) {
			JSDeodorantClassInstantiationsView instantiationsView = ((JSDeodorantClassInstantiationsView)OpenAndAnnotateHelper.openView(JSDeodorantClassInstantiationsView.ID));
			instantiationsView.showInstantiationsFor(typeDeclaration);
		}
	}

}
