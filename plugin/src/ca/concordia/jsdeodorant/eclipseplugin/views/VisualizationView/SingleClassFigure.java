package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;

import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;

public class SingleClassFigure extends ScalableFreeformLayeredPane {

	public SingleClassFigure(ClassDeclaration selectedClass) {
		FreeformLayer formLayer = new FreeformLayer();
		formLayer.setLayoutManager(new FreeformLayout());
		formLayer.add(new ClassFigure(selectedClass), new Rectangle(20, 20, -1, -1));
		add(formLayer, "Primary");
	}

}
