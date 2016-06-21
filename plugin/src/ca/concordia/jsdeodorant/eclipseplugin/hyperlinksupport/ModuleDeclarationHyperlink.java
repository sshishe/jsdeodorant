package ca.concordia.jsdeodorant.eclipseplugin.hyperlinksupport;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotation;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ModuleDeclarationHyperlink implements IHyperlink {
	
	private final IRegion region;
	private final Module module;
	
	public ModuleDeclarationHyperlink(IRegion region, Module module) {
		this.region = region;
		this.module = module;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getHyperlinkText() {
		if (this.module != null) {
			return "JSDeodorant: Open module declaration";
		}
		return null;
	}

	@Override
	public String getTypeLabel() {
		if (this.module != null) {
			return "JSDeodorant: Open module declaration";
		}
		return null;
	}

	@Override
	public void open() {
		OpenAndAnnotateHelper.openEditorAndAnnotate(module.getCanonicalPath(), new JSAnnotation[] {});
	}

}
