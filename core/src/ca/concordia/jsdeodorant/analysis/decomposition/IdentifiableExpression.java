package ca.concordia.jsdeodorant.analysis.decomposition;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;

public interface IdentifiableExpression extends Identifiable {
	
	public void setPublicIdentifier(AbstractIdentifier alias);

	public AbstractIdentifier getPublicIdentifier();
}
