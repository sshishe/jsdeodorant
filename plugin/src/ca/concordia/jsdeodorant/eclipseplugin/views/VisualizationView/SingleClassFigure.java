package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;

public class SingleClassFigure extends ScalableFreeformLayeredPane {

	private final TypeDeclaration typeDeclaration;
	
	private static final int GAP_X = 20;
	private static final int GAP_Y = 35;

	public SingleClassFigure(TypeDeclaration selectedClass) {
		this.typeDeclaration = selectedClass;
		FreeformLayer formLayer = new FreeformLayer();
		formLayer.setLayoutManager(new FreeformLayout());
		add(formLayer, "Primary");
		
		ConnectionLayer connections = new ConnectionLayer();
		add(connections, "Connections");
		AutomaticRouter router = new FanRouter();
		router.setNextRouter(new BendpointConnectionRouter());
		connections.setConnectionRouter(router);
		
		IndividualClassFigure mainClassFigure = new IndividualClassFigure(selectedClass);
		mainClassFigure.setLineWidth(2);
		List<IndividualClassFigure> mainAndParentFigures = new ArrayList<>();
		for (int i = getParentClassesFigures(selectedClass).size() - 1; i >= 0; i--) {
			mainAndParentFigures.add(getParentClassesFigures(selectedClass).get(i));
		}
		mainAndParentFigures.add(mainClassFigure);
		List<IndividualClassFigure> directSubtypes = getDirectSubtypeFigures(selectedClass);
		int lastRowWidth = getDirectSubTypesLevelWidth(directSubtypes);
		int currentY = GAP_Y;
		for (int i = 0; i < mainAndParentFigures.size(); i++) {
			IndividualClassFigure individualClassFigure = mainAndParentFigures.get(i);
			int spaceFromLastRow = lastRowWidth - individualClassFigure.getPreferredSize().width();
			if (spaceFromLastRow < 0) {
				spaceFromLastRow = 0;
			}
			int x = GAP_X + spaceFromLastRow / 2;
			formLayer.add(individualClassFigure, new Rectangle(x, currentY, -1, -1));
			formLayer.validate();
			currentY += individualClassFigure.getPreferredSize().height() + GAP_Y;
			if (i >= 1) {
				IndividualClassFigure parentFigure = mainAndParentFigures.get(i - 1);
				PolylineConnection connection = getInheritanceConnection(individualClassFigure, parentFigure);
				connections.add(connection);
			}
		}
		
		// Add the last row (direct subtypes)
		int currentX = GAP_X;
		for (IndividualClassFigure subtypeIndividualClassFigure : directSubtypes) {
			formLayer.add(subtypeIndividualClassFigure, new Rectangle(currentX, currentY, -1 , -1));
			formLayer.validate();
			connections.add(getInheritanceConnection(subtypeIndividualClassFigure, mainClassFigure));
			if (subtypeIndividualClassFigure.getClassDeclaration().getSubTypes().size() > 0) {
				Label hasChildsLabel = new Label();
				hasChildsLabel.setText("...");
				int labelX = currentX + subtypeIndividualClassFigure.getPreferredSize().width() / 2;
				int labelY = currentY + subtypeIndividualClassFigure.getPreferredSize().height() + GAP_Y;
				formLayer.add(hasChildsLabel, new Rectangle(labelX, labelY, -1, -1));
				formLayer.validate();
				connections.add(getInheritanceConnection(hasChildsLabel, subtypeIndividualClassFigure));
			}
			currentX += subtypeIndividualClassFigure.getPreferredSize().width() + GAP_X;
		}
		
	}

	private PolylineConnection getInheritanceConnection(Figure childCLassFigure, Figure parentClassFigure) {
		PolylineConnection connection = new PolylineConnection();
		connection.setAntialias(SWT.ON);
		int x = childCLassFigure.getLocation().x() + childCLassFigure.getPreferredSize().width() / 2;
		int y = childCLassFigure.getLocation().y();
		XYAnchor sourceAnchor = new XYAnchor(new Point(x, y));
		//ChopboxAnchor sourceAnchor = new ChopboxAnchor(childCLassFigure);
		ChopboxAnchor targetAnchor = new ChopboxAnchor(parentClassFigure);
		connection.setSourceAnchor(sourceAnchor);
		connection.setTargetAnchor(targetAnchor);
		connection.setForegroundColor(Constants.ASSOCIATION_COLOR);
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-2, 2);
		decorationPointList.addPoint(-2, -2);
		decorationPointList.addPoint(0, 0);
		decoration.setTemplate(decorationPointList);
		decoration.setOpaque(true);
		decoration.setBackgroundColor(Constants.ASSOCIATION_COLOR);
		connection.setTargetDecoration(decoration);
		return connection;
	}
	
	public TypeDeclaration getClassDeclaration() {
		return typeDeclaration;
	}

	private List<IndividualClassFigure> getParentClassesFigures(TypeDeclaration selectedClass) {
		List<IndividualClassFigure> toReturn = new ArrayList<>();
		for (TypeDeclaration parentClass : getParentTypes(selectedClass)) {
			IndividualClassFigure classFigure = new IndividualClassFigure(parentClass);
			toReturn.add(classFigure);
		}
		return toReturn;
	}
	
	private List<TypeDeclaration> getParentTypes(TypeDeclaration classDeclaration) {
		List<TypeDeclaration> parents = new ArrayList<>();
		while (classDeclaration.getSuperType() != null) {
			TypeDeclaration parent = classDeclaration.getSuperType();
			parents.add(parent);
			classDeclaration = parent;
		}
		return parents;
	}
	
	private List<IndividualClassFigure> getDirectSubtypeFigures(TypeDeclaration selectedClass) {
		List<IndividualClassFigure> toReturn = new ArrayList<>();
		for (TypeDeclaration childClass : selectedClass.getSubTypes()) {
			IndividualClassFigure childClassFigure = new IndividualClassFigure(childClass);
			toReturn.add(childClassFigure);
		}
		return toReturn;
	}
	
	private int getDirectSubTypesLevelWidth(List<IndividualClassFigure> directSubtypes) {
		int width = 0;
		for (Iterator<IndividualClassFigure> iterator = directSubtypes.iterator(); iterator.hasNext();) {
			IndividualClassFigure individualClassFigure = iterator.next();
			width += individualClassFigure.getPreferredSize().width();
			if (iterator.hasNext()) {
				width += GAP_X;
			}
		}
		return width;
	}

}
