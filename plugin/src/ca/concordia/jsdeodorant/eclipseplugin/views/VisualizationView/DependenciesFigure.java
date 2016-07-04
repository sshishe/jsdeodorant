package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;

public class DependenciesFigure extends ScalableFreeformLayeredPane {

	public DependenciesFigure(Module selectedModule) {
		
		final int GAP = 20;
		final int MAIN_MODULE_X = 20;
		
		setFont(Display.getDefault().getSystemFont());
		
		FreeformLayer formLayer = new FreeformLayer();
		formLayer.setLayoutManager(new FreeformLayout());
		
		ConnectionLayer connections = new ConnectionLayer();
		
		ModuleFigure thisModuleFigure = new ModuleFigure(selectedModule);
		
		int totalDepdendencyHeight = 0;
		
		List<ModuleFigure> dependecnyModules = new ArrayList<>();
		for (Dependency dependency : selectedModule.getDependencies()) {
			ModuleFigure moduleFigure = new ModuleFigure(dependency.getDependency());
			dependecnyModules.add(moduleFigure);
			totalDepdendencyHeight += GAP + moduleFigure.getPreferredSize().height;
		}
		
		int mainModuleY = (totalDepdendencyHeight + GAP - thisModuleFigure.getPreferredSize().height) / 2; 
		formLayer.add(thisModuleFigure, new Rectangle(MAIN_MODULE_X, mainModuleY, -1, -1));
		
		int x = MAIN_MODULE_X + thisModuleFigure.getPreferredSize().width + GAP;
		int y = GAP;
		
		for (ModuleFigure moduleFigure : dependecnyModules) {
			formLayer.add(moduleFigure, new Rectangle(x  , y, -1, -1));
			y += moduleFigure.getPreferredSize().height + GAP;
			
			PolylineConnection connection = new PolylineConnection();
			ChopboxAnchor sourceAnchor = new ChopboxAnchor(thisModuleFigure);
			ChopboxAnchor targetAnchor = new ChopboxAnchor(moduleFigure);
			connection.setSourceAnchor(sourceAnchor);
			connection.setTargetAnchor(targetAnchor);
			connection.setBackgroundColor(Constants.ASSOCIATION_COLOR);
			PolylineDecoration decoration = new PolylineDecoration();
			PointList decorationPointList = new PointList();
			decorationPointList.addPoint(-1, -1);
			decorationPointList.addPoint(0, 0);
			decorationPointList.addPoint(-1, 1);
			decoration.setTemplate(decorationPointList);
			decoration.setBackgroundColor(Constants.ASSOCIATION_COLOR);
			connection.setTargetDecoration(decoration);
			connections.add(connection);
		}
		
		add(formLayer, "Primary");
		add(connections,  "Connections");
		
	}
	
}
