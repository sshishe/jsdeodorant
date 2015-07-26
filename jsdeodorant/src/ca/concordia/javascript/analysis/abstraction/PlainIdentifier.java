package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class PlainIdentifier extends AbstractIdentifier {
	private volatile int hashCode = 0;

	public PlainIdentifier(Token token) {
		super(token);
	}

	public PlainIdentifier(ParseTree node) {
		super(node);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof PlainIdentifier) {
			AbstractIdentifier plain = (AbstractIdentifier) o;
			if (this.identifierName == null) // Mostly happens when we have new
												// function(){..} for other
												// cases, finding the name is
												// almost handled
				return false;
			return this.identifierName.equals(plain.identifierName);
		} else if (o != null)
			return this.toString().equals(o.toString());
		else
			return false;
	}

	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			if (getNode() != null)
				result = 31 * result + getNode().hashCode();
			else
				result = 31 * result + token.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	public String toString() {
		return identifierName;
	}
}
