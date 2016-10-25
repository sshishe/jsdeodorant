package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.util.EnumSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.MethodType;
import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;

public class Constants {
	
	public static final String DEPENDENCIES_ICON_IMAGE = "search_decl_obj.png";
	public static final String METHOD_ICON_IMAGE = "methpub_obj.png";
	public static final String FIELD_ICON_IMAGE = "field_public_obj.png";
	public static final String CLASS_ICON_IMAGE = "class_obj.png";
	public static final String PACKAGE_ICON_IMAGE = "package_obj.png";
	public static final String JS_FILE_ICON_IMAGE = "js_file.gif";
	private static final String ABSTRACT_OVERLAY = "abstract_overlay.png";
	private static final String PROTOTYPE_OVERLAY = "prototype_overlay.png";
	private static final String OVERRIDING_OVERLAY = "overriding_overlay.png";
	private static final String OVERRIDDEN_OVERLAY = "overridden_overlay.png";
	public static final String MODULES_VIEW_ICON = "package-explorer.gif";
	public static final String TYPE_HIERARCHY_VIEW_ICON = "hierarchy_view.gif";
	public static final String FOLDER_ICON_IMAGE = "folder_icon.png";
	public static final String SEARCH_FOR_REFERENCES_ICON = "search_ref_obj.png";
	public static final String ICON_PATH = "icons";
	
	public static final String JAVASCRIPT_PROJECT_NATURE = "org.eclipse.wst.jsdt.core.jsNature";
	
	public static final Color CLASS_DIAGRAM_CLASS_COLOR = new Color(Display.getCurrent(), new RGB(250, 250, 180));
	public static final Color CLASS_DIAGRAM_MODULE_COLOR = new Color(Display.getCurrent(), new RGB(235, 235, 235));
	public static final Color ASSOCIATION_COLOR = new Color(Display.getCurrent(), new RGB(100, 100, 100));
	
	
	public static Image getMethodImage(Method method) {
		ImageDescriptor descriptor = JSDeodorantPlugin.getImageDescriptor(METHOD_ICON_IMAGE);
		EnumSet<MethodType> kinds = method.getKinds();
		if (kinds.contains(MethodType.abstractMethod)) {
			ImageDescriptor overlay = JSDeodorantPlugin.getImageDescriptor(ABSTRACT_OVERLAY);
			Point overlayPosition = new Point(descriptor.getImageData().width - overlay.getImageData().width, 0);
			descriptor = JSDeodorantPlugin.getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.declaredOutOfClassBody)) {
			ImageDescriptor overlay = JSDeodorantPlugin.getImageDescriptor(PROTOTYPE_OVERLAY);
			Point overlayPosition = new Point(descriptor.getImageData().width - overlay.getImageData().width, 
					descriptor.getImageData().height - overlay.getImageData().height);
			descriptor = JSDeodorantPlugin.getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.overriden)) {
			ImageDescriptor overlay = JSDeodorantPlugin.getImageDescriptor(OVERRIDING_OVERLAY);
			Point overlayPosition = new Point(0, descriptor.getImageData().height - overlay.getImageData().height);
			descriptor = JSDeodorantPlugin.getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.overriding)) {
			ImageDescriptor overlay = JSDeodorantPlugin.getImageDescriptor(OVERRIDDEN_OVERLAY);
			Point overlayPosition = new Point(0, 1);
			descriptor = JSDeodorantPlugin.getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);			
		}
		return descriptor.createImage();
	}
}
