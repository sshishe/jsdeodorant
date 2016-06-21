package ca.concordia.jsdeodorant.eclipseplugin.annotations;

public enum JSAnnotationType {
	
	ANNOTATION("ca.concordia.cssanalyser.plugin.annotations.Annotation");
	
	private String value;
		
	private JSAnnotationType(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}

}