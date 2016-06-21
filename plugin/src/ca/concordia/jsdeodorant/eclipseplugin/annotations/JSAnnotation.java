package ca.concordia.jsdeodorant.eclipseplugin.annotations;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

public class JSAnnotation extends Annotation {
	
	private final Position position;
	
	public JSAnnotation(JSAnnotationType type, String text, Position position) {
		super(type.toString(), false, text);
		this.position = position;
	}
	
	public Position getPosition() {
		return this.position;
	}
}
