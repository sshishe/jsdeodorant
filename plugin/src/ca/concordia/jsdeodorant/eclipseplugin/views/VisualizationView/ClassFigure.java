package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;

import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo;
import ca.concordia.jsdeodorant.eclipseplugin.util.MethodAttributeInfo.Type;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ClassFigure extends RoundedRectangle {

	public ClassFigure(ClassDeclaration classDeclaration) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(5);
		setLayoutManager(layout);	

		setBorder(new MarginBorder(2, 5, 2, 5));
		setBackgroundColor(Constants.CLASS_DIAGRAM_CLASS_COLOR);
		setOpaque(true);

		Label className = new Label(classDeclaration.getName(), 
				JSDeodorantPlugin.getImageDescriptor(Constants.CLASS_ICON_IMAGE).createImage());
		add(className);

		Map<String, AbstractExpression> attributeExpressions = classDeclaration.getAttributes();
		if (attributeExpressions.size() > 0) {
			List<MethodAttributeInfo> fields = new ArrayList<>();
			for (String attribute : attributeExpressions.keySet()) {
				fields.add(new MethodAttributeInfo(attribute, attributeExpressions.get(attribute), classDeclaration, Type.ATTRIBUTE));
			}
			CompartmentFigure fieldFigure = new CompartmentFigure(fields);
			add(fieldFigure);
		}

		Map<String, AbstractExpression> methodExpressions = classDeclaration.getMethods();
		if (methodExpressions.size() > 0) {
			List<MethodAttributeInfo> methods = new ArrayList<>();
			for (String method : methodExpressions.keySet()) {
				methods.add(new MethodAttributeInfo(method, methodExpressions.get(method), classDeclaration, Type.METHOD));
			}
			CompartmentFigure methodFigure = new CompartmentFigure(methods);
			add(methodFigure);
		}

		addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				OpenAndAnnotateHelper.openAndAnnotateClassDeclaration(classDeclaration);
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
