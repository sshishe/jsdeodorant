package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import ca.concordia.jsdeodorant.analysis.abstraction.Dependency;
import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;

public class DependenciesFigure extends ScalableFreeformLayeredPane {
	
	static final int GAP_X = 100;
	static final int GAP_Y = 20;
	static final int MAIN_MODULE_X = 20;
	
	public DependenciesFigure(Module selectedModule) {

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
			totalDepdendencyHeight += GAP_Y + moduleFigure.getPreferredSize().height();
		}
		
		int mainModuleY = (totalDepdendencyHeight + GAP_Y - thisModuleFigure.getPreferredSize().height()) / 2; 
		formLayer.add(thisModuleFigure, new Rectangle(MAIN_MODULE_X, mainModuleY, -1, -1));
		
		int x = MAIN_MODULE_X + thisModuleFigure.getPreferredSize().width() + GAP_X;
		int y = GAP_Y;
		
		for (ModuleFigure moduleFigure : dependecnyModules) {
			formLayer.add(moduleFigure, new Rectangle(x, y, -1, -1));
			y += moduleFigure.getPreferredSize().height() + GAP_Y;
			
			formLayer.validate();
			
			PolylineConnection connection = new PolylineConnection();
			connection.setAntialias(SWT.ON);
			int srcX = thisModuleFigure.getLocation().x() + thisModuleFigure.getPreferredSize().width();
			int srcY = thisModuleFigure.getLocation().y() + thisModuleFigure.getPreferredSize().height() / 2;
			XYAnchor sourceAnchor = new XYAnchor(new Point(srcX, srcY));
			int destX = moduleFigure.getLocation().x();
			int destY = moduleFigure.getLocation().y() + moduleFigure.getPreferredSize().height() / 2;
			XYAnchor targetAnchor = new XYAnchor(new Point(destX, destY));
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
