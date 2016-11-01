package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.javascript.jscomp.parsing.parser.util.SourceRange;

import ca.concordia.jsdeodorant.analysis.abstraction.ObjectCreation;
import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeMember;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotation;
import ca.concordia.jsdeodorant.eclipseplugin.annotations.JSAnnotationType;

public class OpenAndAnnotateHelper {

	public static void openEditorAndAnnotate(String filePath, JSAnnotation... annotations) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IEditorPart editorPart;
		try {
			IPath path = new Path(filePath);
			editorPart = IDE.openEditor(page, ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path));
			IAnnotationModel annotationModel = clearAnnotations(editorPart);
			if (annotations != null && annotations.length > 0) {
				for (JSAnnotation annotation : annotations) {
					annotationModel.addAnnotation(annotation, annotation.getPosition());
				}
				ITextEditor iTextEditor = ((ITextEditor)editorPart);
				iTextEditor.getSelectionProvider().
					setSelection(new TextSelection(iTextEditor.getDocumentProvider().getDocument(iTextEditor), 
							annotations[0].getPosition().getOffset(), 0));
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}	
	}

	public static IAnnotationModel clearAnnotations(IEditorPart editorPart) {
		IAnnotationModel annotationModel = ((ITextEditor)editorPart).getDocumentProvider().getAnnotationModel(editorPart.getEditorInput());
		Iterator<?> annotationIterator = annotationModel.getAnnotationIterator();
		while (annotationIterator.hasNext()) {
			Annotation annotation = (Annotation)annotationIterator.next();
			if (annotation instanceof JSAnnotation) {
				annotationModel.removeAnnotation(annotation);
			}
		}
		return annotationModel;
	}

	public static void openEditorAndAnnotate(String filePath, JSAnnotation annotation) {
		openEditorAndAnnotate(filePath, new JSAnnotation[] { annotation });
	}

	public static void openAndAnnotateClassDeclaration(TypeDeclaration typeDeclaration) {
		SourceRange location = typeDeclaration.getFunctionDeclaration().getFunctionDeclarationTree().location;
		String filePath = location.start.source.name;
		Position postion = new Position(location.start.offset, location.end.offset - location.start.offset + 1);
		JSAnnotation annotation = new JSAnnotation(JSAnnotationType.ANNOTATION, "Class declaration", postion);
		openEditorAndAnnotate(filePath, annotation);
	}

	public static void openAndAnnotateMethodOrAttribute(TypeMember typeMember) {
		SourceRange location = typeMember.getParseTree().location;
		String filePath = location.start.source.name;
		Position postion = new Position(location.start.offset, location.end.offset - location.start.offset + 1);
		String annotationText = (typeMember instanceof Method ? "Method" : "Attribute") + " declaration";
		JSAnnotation annotation = new JSAnnotation(JSAnnotationType.ANNOTATION, annotationText, postion);
		openEditorAndAnnotate(filePath, annotation);
	}
	
	public static void openAndAnnotateObjectCreation(ObjectCreation objectCreation) {
		SourceRange location = objectCreation.getNewExpressionTree().location;
		String filePath = location.start.source.name;
		Position postion = new Position(location.start.offset, location.end.offset - location.start.offset + 1);
		String annotationText = "Class instantiation";
		if (objectCreation.getClassDeclaration() != null) {
			annotationText = "Instantiation of the class " + objectCreation.getClassDeclaration().toString();
		}
		JSAnnotation annotation = new JSAnnotation(JSAnnotationType.ANNOTATION, annotationText, postion);
		openEditorAndAnnotate(filePath, annotation);
	}

	public static IEditorPart getActiveEditor() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			IEditorPart activeEditor = page.getActiveEditor();
			return activeEditor;
		}
		return null;
	}
	
	public static IViewPart openView(String viewID) {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static IViewPart getView(String viewID) {
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (viewID.equals(viewReferences[i].getId())) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

}
