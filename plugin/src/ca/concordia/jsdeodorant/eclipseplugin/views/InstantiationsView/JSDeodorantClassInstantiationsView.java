package ca.concordia.jsdeodorant.eclipseplugin.views.InstantiationsView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.eclipseplugin.util.ModulesInfo;
import ca.concordia.jsdeodorant.eclipseplugin.views.ModulesView.ClassesTreeViewerComparator;

public class JSDeodorantClassInstantiationsView extends ViewPart {
	
	public static final String ID = "jsdeodorant-eclipse-plugin.JSDeodorantInstantiationsView";

	private TreeViewer classTreeViewer;
	private TypeDeclaration typeDeclaration;
	private Label typeDeclarationNameLabel;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		Composite nameLayout = new Composite(parent, SWT.NONE);
		nameLayout.setLayout(new GridLayout(2, false));
		Label titleLabel = new Label(nameLayout, SWT.NONE);
		titleLabel.setText("Instantiations of the class");
		
		typeDeclarationNameLabel = new Label(nameLayout, SWT.NONE);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(titleLabel.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(titleLabel.getDisplay());
		typeDeclarationNameLabel.setFont(boldFont);
		
		classTreeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		classTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		ClassInstantiationsTreeViewerContentProvider contentProvider = new ClassInstantiationsTreeViewerContentProvider(new ArrayList<>());
		classTreeViewer.setContentProvider(contentProvider);
		classTreeViewer.setLabelProvider(new ClassInstantiationsTreeViewerLabelProvider(contentProvider));
		classTreeViewer.addDoubleClickListener(new ClassInstantiationsTreeViewerDoubleClickListener());
		classTreeViewer.setInput(getViewSite());
		classTreeViewer.setComparator(new ClassesTreeViewerComparator());
	}

	@Override
	public void setFocus() {
		if (classTreeViewer != null) {
			classTreeViewer.getControl().setFocus();
		}
	}

	public void showInstantiationsFor(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
		findInstantiations();
	}

	private void findInstantiations() {
		List<ObjectCreation> objectCreationsList = new ArrayList<>();
		for (Module module : ModulesInfo.getModuleInfo()) {
			Program program = module.getProgram();
			for (ObjectCreation objectCreation : program.getObjectCreationList()) {
				// Just because we don't have ClassDeclaration#equals()
				if (objectCreation.getClassDeclaration() != null &&
						objectCreation.getClassDeclaration().getName().equals(typeDeclaration.getName()) &&
						objectCreation.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree().location ==
							typeDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location) {
					objectCreationsList.add(objectCreation);
				}
			}
		}
		
		ClassInstantiationsTreeViewerContentProvider contentProvider = new ClassInstantiationsTreeViewerContentProvider(objectCreationsList);
		classTreeViewer.setContentProvider(contentProvider);
		classTreeViewer.setLabelProvider(new ClassInstantiationsTreeViewerLabelProvider(contentProvider));
		Module module = typeDeclaration.getParentModule();
		String moduleParentName = ModulesInfo.getModuleParentName(module);
		if (!moduleParentName.endsWith("/")) {
			moduleParentName += "/";
		}
		String moduleName = (new File(module.getSourceFile().getName())).getName() + "/";
		typeDeclarationNameLabel.setText(moduleParentName + moduleName + typeDeclaration.getName());
		typeDeclarationNameLabel.getParent().getParent().layout();
	}

}
