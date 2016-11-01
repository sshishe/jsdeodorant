package ca.concordia.jsdeodorant.eclipseplugin.views.VisualizationView;

import java.io.File;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotation;
import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;
import ca.concordia.jsdeodorant.eclipseplugin.util.ImagesHelper;
import ca.concordia.jsdeodorant.eclipseplugin.util.OpenAndAnnotateHelper;

public class ModuleFigure extends Figure {
	
	public ModuleFigure(Module selectedModule) {
		ToolbarLayout toolbarLayout = new ToolbarLayout();
		toolbarLayout.setSpacing(5);
		setLayoutManager(toolbarLayout);
		setBorder(new MarginBorder(2, 5, 2, 5));
		setBackgroundColor(Constants.CLASS_DIAGRAM_MODULE_COLOR);
		setOpaque(true);
		
		Label moduleName = new Label(getModuleName(selectedModule),
				ImagesHelper.getImageDescriptor(Constants.JS_FILE_ICON_IMAGE).createImage());
		moduleName.setLabelAlignment(PositionConstants.LEFT);
		moduleName.setToolTip(new Label(selectedModule.getSourceFile().getName()));
		add(moduleName);
		
		Figure classesPart = new Figure();
		classesPart.setLayoutManager(new FlowLayout());
		add(classesPart);
		
		for (TypeDeclaration classDeclaration : selectedModule.getTypes()) {
			classesPart.add(new IndividualClassFigure(classDeclaration, false));
		}
		
		addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				OpenAndAnnotateHelper.openEditorAndAnnotate(selectedModule.getSourceFile().getOriginalPath(), new JSAnnotation[] {});
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

	public String getModuleName(Module selectedModule) {
		String selectedModuleName = (new File(selectedModule.getSourceFile().getName())).getName();
		selectedModuleName = selectedModuleName.substring(0, selectedModuleName.indexOf(".")).replace("-", "_");
		return selectedModuleName;
	}
}
