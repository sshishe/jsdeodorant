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
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class IndividualClassFigure extends RoundedRectangle {

	private final TypeDeclaration typeDeclaration;

	public IndividualClassFigure(TypeDeclaration classDeclaration) {
		this(classDeclaration, true);
	}
	
	public IndividualClassFigure(TypeDeclaration typeDeclaration, boolean showClassMembers) {
		this.typeDeclaration = typeDeclaration;
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(5);
		setLayoutManager(layout);	

		setBorder(new MarginBorder(10, 5, 2, 5));
		setBackgroundColor(Constants.CLASS_DIAGRAM_CLASS_COLOR);
		setOpaque(true);
		setAntialias(SWT.ON);

		Label className = new Label(typeDeclaration.getName(), 
				ImagesHelper.getTypeImage(typeDeclaration));
		add(className);

		if (showClassMembers) {
			List<TypeMember> attributes = typeDeclaration.getTypeMembers().stream()
					.filter(member -> member instanceof Attribute).collect(Collectors.toList());

			List<TypeMember> methods = typeDeclaration.getTypeMembers().stream()
					.filter(member -> member instanceof Method).collect(Collectors.toList());

			if (attributes.size() > 0) {
				CompartmentFigure fieldFigure = new CompartmentFigure(attributes);
				add(fieldFigure);
			}

			if (methods.size() > 0) {
				CompartmentFigure methodFigure = new CompartmentFigure(methods);
				add(methodFigure);
			}
		}
		
		IndividualClassToolsFigure toolsFigure = new IndividualClassToolsFigure(typeDeclaration);
		add(toolsFigure);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				OpenAndAnnotateHelper.openAndAnnotateClassDeclaration(typeDeclaration);
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

	public TypeDeclaration getClassDeclaration() {
		return typeDeclaration;
	}
	
}
