package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.jsdeodorant.analysis.abstraction.Module; 

public class ModulesInfo {
	
	private static List<Module> detectedModules;
	
	private ModulesInfo() {
		
	}
	
	public static List<Module> getModuleInfo() {
		if (detectedModules == null) {
			return new ArrayList<>();
		}
		return detectedModules;
	}

	public static void setModuleInfo(List<Module> modules) {
		detectedModules = modules;
	}
}
