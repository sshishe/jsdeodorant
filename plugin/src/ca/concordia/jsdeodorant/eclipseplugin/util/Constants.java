package ca.concordia.jsdeodorant.eclipseplugin.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Constants {
	
	public enum ViewID {
		MODULES_VIEW("jsdeodorant-eclipse-plugin.JSDeodorantModulesView"),
		VISUALIZATION_VIEW("jsdeodorant-eclipse-plugin.JSDeodorantVisualizationView");
		
		private final String viewID;

		private ViewID(String viewID) {
			this.viewID = viewID;
		}

		public String getViewID() {
			return viewID;
		}
	}
	
	public static final String DEPENDENCIES_ICON_IMAGE = "search_decl_obj.png";
	public static final String METHOD_ICON_IMAGE = "methpub_obj.png";
	public static final String FIELD_ICON_IMAGE = "field_public_obj.png";
	public static final String CLASS_ICON_IMAGE = "class_obj.png";
	public static final String PACKAGE_ICON_IMAGE = "package_obj.png";
	public static final String ICON_PATH = "icons";
	public static final String JAVASCRIPT_PROJECT_NATURE = "org.eclipse.wst.jsdt.core.jsNature";
	
	public static final Color CLASS_DIAGRAM_CLASS_COLOR = new Color(Display.getCurrent(), new RGB(245, 245, 150));
	public static final Color CLASS_DIAGRAM_MODULE_COLOR = new Color(Display.getCurrent(), new RGB(235, 235, 235));
	public static final Color ASSOCIATION_COLOR = new Color(Display.getCurrent(), new RGB(100, 100, 100));
}
