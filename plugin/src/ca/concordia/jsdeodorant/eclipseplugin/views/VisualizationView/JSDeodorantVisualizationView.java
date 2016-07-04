package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;

public class JSDeodorantVisualizationView extends ViewPart {
	
	private FigureCanvas figureCanvas;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		figureCanvas = new FigureCanvas(parent, SWT.DOUBLE_BUFFERED);
		figureCanvas.setBackground(ColorConstants.white);
	}

	public void showDependenciesGraph(Module selectedModule) {
		if (selectedModule != null) {
			figureCanvas.setViewport(new FreeformViewport());
			figureCanvas.setContents(new DependenciesFigure(selectedModule));
		}
	}

	public void showUMLClassDiagram(ClassDeclaration selectedClass) {
		if (selectedClass != null) {
			figureCanvas.setViewport(new FreeformViewport());
			figureCanvas.setContents(new SingleClassFigure(selectedClass));
		}
	}
	
	@Override
	public void setFocus() {}

}
