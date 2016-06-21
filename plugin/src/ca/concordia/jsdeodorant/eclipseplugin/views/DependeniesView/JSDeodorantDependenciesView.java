package ca.concordia.jsdeodorant.eclipseplugin.views.DependeniesView;

import java.io.File;

import org.eclipse.gef4.dot.DotImport;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.ui.IViewPart;

import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants.ViewID;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;
import javafx.scene.image.Image;

@SuppressWarnings("restriction")
public class JSDeodorantDependenciesView extends ZestFxUiView {

	public void showDependenciesGraph(Module selectedModule) {

		if (selectedModule != null) {
			String dotCode = getDotCodeForModuleDependencies(selectedModule);
			Graph graph = new DotImport(dotCode).newGraphInstance();
			SpringLayoutAlgorithm algorithm = new SpringLayoutAlgorithm();
			ZestProperties.setLayout(graph, algorithm);
			for (Node node : graph.getNodes()) {
				ZestProperties.setNodeRectCssStyle(node, "-fx-fill: #666");
				//ZestProperties.setNodeTextCssStyle(node, "-fx-font-weight: bold; -fx-font-size: 14px; color: red");
				ZestProperties.setIcon(node, new Image(Constants.ICON_PATH + "/" + Constants.DEPENDENCIES_ICON_IMAGE));
			}
			setGraph(graph);
		}
	}

	private String getDotCodeForModuleDependencies(Module... selectedModules) {
		StringBuilder dotCode = new StringBuilder();
		dotCode.append("digraph Dependencies {").append(System.lineSeparator());
		dotCode.append("edge[style=dashed]").append(System.lineSeparator());
		dotCode.append("node[shape=record]").append(System.lineSeparator()); // Currently does not work :(
		for (Module module : selectedModules) {
			addModuleDependencyNodes(module, dotCode);
		}
		dotCode.append("}");
		return dotCode.toString();
	}

	public void addModuleDependencyNodes(Module selectedModule, StringBuilder dotCode) {
		String selectedModuleName = getModuleName(selectedModule);
		for (Dependency dependency : selectedModule.getDependencies()) {
			dotCode.append(selectedModuleName);
			dotCode.append("->");
			dotCode.append(getModuleName(dependency.getDependency()));
			dotCode.append(";").append(System.lineSeparator());
		}
	}

	public String getModuleName(Module selectedModule) {
		String selectedModuleName = (new File(selectedModule.getSourceFile().getName())).getName();
		selectedModuleName = selectedModuleName.substring(0, selectedModuleName.indexOf(".")).replace("-", "_");
		return selectedModuleName;
	}

	@SuppressWarnings("unused")
	private Module getSelectedModuleInModulesView() {
		IViewPart view = OpenAndAnnotateHelper.getView(ViewID.MODULES_VIEW);
		if (view != null) {
			JSDeodorantModulesView modulesView = (JSDeodorantModulesView)view;
			Module selectedModule = modulesView.getSelectedModule();
			return selectedModule;
		}
		return null;
	}

}
