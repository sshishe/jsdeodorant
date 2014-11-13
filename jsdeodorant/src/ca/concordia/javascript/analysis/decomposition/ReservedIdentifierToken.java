package ca.concordia.javascript.analysis.decomposition;

public enum ReservedIdentifierToken {
	Array;

	public static boolean contains(String token) {

		for (ReservedIdentifierToken c : ReservedIdentifierToken.values()) {
			if (c.name().equals(token)) {
				return true;
			}
		}
		return false;
	}
}
