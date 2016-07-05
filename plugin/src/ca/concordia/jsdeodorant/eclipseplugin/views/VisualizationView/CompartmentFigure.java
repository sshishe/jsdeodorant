package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.List;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class CompartmentFigure extends Figure {
	
	public CompartmentFigure(List<MethodAttributeInfo> entities) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(1);
		setLayoutManager(layout);
		setBorder(new AbstractBorder() {				
			@Override
			public void paint(IFigure figure, Graphics graphics, Insets insets) {
				graphics.setForegroundColor(Constants.ASSOCIATION_COLOR);
				graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
			}
			
			@Override
			public Insets getInsets(IFigure arg0) {
				return new Insets(1, 0, 0, 0);
			}
		});
		
		for (MethodAttributeInfo entity : entities) {
			String image = "";
			switch (entity.getType()) {
				case ATTRIBUTE:
					image = Constants.FIELD_ICON_IMAGE;
					break;
				case METHOD:
					image = Constants.METHOD_ICON_IMAGE;
					break;
				default:					
			}
			Label label = new Label(entity.getName(), JSDeodorantPlugin.getImageDescriptor(image).createImage());
			add(label);
			label.addMouseListener(new MouseListener() {
				@Override
				public void mouseDoubleClicked(MouseEvent me) {
					OpenAndAnnotateHelper.openAndAnnotateMethodOrAttribute(entity);
				}

				@Override
				public void mousePressed(MouseEvent me) {
					me.consume();
				}

				@Override
				public void mouseReleased(MouseEvent me) {
					me.consume();
				}
				
			});
		}
		
	}
}