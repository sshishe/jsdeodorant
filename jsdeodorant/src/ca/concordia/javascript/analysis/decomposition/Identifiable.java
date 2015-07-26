package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;

public interface Identifiable {
	public AbstractIdentifier getIdentifier();

	public String getName();
}
