package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;

public interface IdentifiableExpression extends Identifiable {
	public String getQualifiedName();

	public void setPublicIdentifier(AbstractIdentifier alias);

	public AbstractIdentifier getPublicIdentifier();
}
