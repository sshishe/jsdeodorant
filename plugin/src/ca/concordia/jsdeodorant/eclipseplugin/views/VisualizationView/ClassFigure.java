package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;

import ca.concordia.jsdeodorant.analysis.decomposition.Attribute;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassMember;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ClassFigure extends RoundedRectangle {

	public ClassFigure(ClassDeclaration classDeclaration) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(5);
		setLayoutManager(layout);	

		setBorder(new MarginBorder(10, 5, 2, 5));
		setBackgroundColor(Constants.CLASS_DIAGRAM_CLASS_COLOR);
		setOpaque(true);
		setAntialias(SWT.ON);

		Label className = new Label(classDeclaration.getName(), 
				JSDeodorantPlugin.getImageDescriptor(Constants.CLASS_ICON_IMAGE).createImage());
		add(className);

		List<ClassMember> attributes = classDeclaration.getClassMembers().stream()
				.filter(member -> member instanceof Attribute).collect(Collectors.toList());
		
		List<ClassMember> methods = classDeclaration.getClassMembers().stream()
			.filter(member -> member instanceof Method).collect(Collectors.toList());
		
		if (attributes.size() > 0) {
			CompartmentFigure fieldFigure = new CompartmentFigure(attributes);
			add(fieldFigure);
		}

		if (methods.size() > 0) {
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
