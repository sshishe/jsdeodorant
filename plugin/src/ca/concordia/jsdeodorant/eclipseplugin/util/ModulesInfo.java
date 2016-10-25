package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.concordia.jsdeodorant.analysis.abstraction.Module; 

public class ModulesInfo {
	
	private static List<Module> detectedModules;
	private static String rootDirectory;
	
	private ModulesInfo() {
		
	}
	
	public static List<Module> getModuleInfo() {
		if (detectedModules == null) {
			return new ArrayList<>();
		}
		return detectedModules;
	}

	public static void setModuleInfo(List<Module> modules, String rootDirectory) {
		detectedModules = modules;
		ModulesInfo.rootDirectory = rootDirectory;
	}
	
	public static String getModuleParentName(Module module) {
		File parentFile = new File(module.getSourceFile().getOriginalPath()).getParentFile();
		String parentFolderName = "";
		if (parentFile != null) {
			try {
				parentFolderName = parentFile.getCanonicalFile().getAbsolutePath().replace(rootDirectory, "").replace("\\", "/");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("".equals(parentFolderName.trim())) {
			parentFolderName = "/";
		}
		return parentFolderName;
	}
	
	public static String getProjectRootDirectory() {
		return rootDirectory;
	}
}
