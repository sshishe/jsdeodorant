package ca.concordia.jsdeodorant.eclipseplugin.util;

public class Constants {
	
	public enum ViewID {
		MODULES_VIEW("jsdeodorant-eclipse-plugin.JSDeodorantModulesView"),
		DEPENDENCIES_VIEW("jsdeodorant-eclipse-plugin.JSDeodorantDependenciesView");
		
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
}
