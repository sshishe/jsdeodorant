package ca.concordia.javascript.language;

import java.util.ArrayList;
import java.util.List;

public class PredefinedFunction {
	private String objectName;
	private List<String> functions;

	public PredefinedFunction(String name) {
		this.objectName = name;
		this.functions = new ArrayList<>();
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String name) {
		this.objectName = name;
	}

	public List<String> getFunctions() {
		return functions;
	}

	public void addFunction(String functionName) {
		this.functions.add(functionName);
	}

	public boolean contains(String functionName) {
		for (String string : functions) {
			if (string.equalsIgnoreCase(functionName))
				return true;
		}
		return false;
	}
}
