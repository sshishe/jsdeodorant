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
import org.eclipse.swt.graphics.Image;

import ca.concordia.jsdeodorant.analysis.decomposition.Attribute;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class CompartmentFigure extends Figure {
	
	public CompartmentFigure(List<TypeMember> entities) {
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
				return new Insets(5, 0, 5, 0);
			}
		});
		
		for (TypeMember entity : entities) {
			Image image;
			if (entity instanceof Attribute) {
				image = ImagesHelper.getImageDescriptor(Constants.FIELD_ICON_IMAGE).createImage();
			} else {
				image = ImagesHelper.getMethodImage((Method)entity);
			}
			Label label = new Label(entity.getName(), image);
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