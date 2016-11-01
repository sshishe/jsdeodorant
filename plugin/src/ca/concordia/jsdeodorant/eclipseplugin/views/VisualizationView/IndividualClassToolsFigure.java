package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;

import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;
import ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView.JSDeodorantClassInstantiationsView;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.JSDeodorantModulesView;

public class IndividualClassToolsFigure extends Figure {
	
	private static class MyImageButton extends ImageFigure {
		
		public MyImageButton(String tooltipText, String icon, Runnable runnable) {
			super(ImagesHelper.getImageDescriptor(icon).createImage());
			setToolTip(new Label(tooltipText));
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					runnable.run();
				}
				
				@Override
				public void mouseDoubleClicked(MouseEvent arg0) {}
			});
		}
		
	}

	public IndividualClassToolsFigure(TypeDeclaration declaration) {
		GridLayout layout = new GridLayout(3, true);
		setLayoutManager(layout);
		
		add(new MyImageButton("Show class diagram", 
				Constants.DEPENDENCIES_ICON_IMAGE,
				() -> ((JSDeodorantVisualizationView)OpenAndAnnotateHelper.openView(JSDeodorantVisualizationView.ID)).showUMLClassDiagram(declaration)
				));
		
		add(new MyImageButton("Find instantiations",
				Constants.SEARCH_FOR_REFERENCES_ICON,
				() -> ((JSDeodorantClassInstantiationsView)OpenAndAnnotateHelper.openView(JSDeodorantClassInstantiationsView.ID)).showInstantiationsFor(declaration)
				));
		
		add(new MyImageButton("Show type hierarchy",
				Constants.TYPE_HIERARCHY_VIEW_ICON,
				() -> ((JSDeodorantModulesView)OpenAndAnnotateHelper.openView(JSDeodorantModulesView.ID)).showTypeHierarchyForClassDeclaration(declaration)
				));
	}
	
}
