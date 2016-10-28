package ca.concordia.jsdeodorant.analysis;

public enum AnalysisStep {
	BUILDING_MODEL("Building hierarchical model"),
	IDENTIFYING_CLASSES("Identifying class declarations"),
	BUILDING_CLASS_HIERARCHIES("Building class hierarchies");
	
	private final String description;
	private AnalysisStep(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
