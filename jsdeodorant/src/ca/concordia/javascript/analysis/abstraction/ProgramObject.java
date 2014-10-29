package ca.concordia.javascript.analysis.abstraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramObject {
	private List<FunctionDeclarationObject> functionDeclarationList;
	private Map<String, FunctionDeclarationObject> functionNameMap;

	public ProgramObject() {
		functionDeclarationList = new ArrayList<FunctionDeclarationObject>();
	}

	public void AddFunctionDeclaration(FunctionDeclarationObject functionDeclarationObject) {
		functionNameMap.put(functionDeclarationObject.getName(),
				functionDeclarationObject);
		functionDeclarationList.add(functionDeclarationObject);
	}
}
