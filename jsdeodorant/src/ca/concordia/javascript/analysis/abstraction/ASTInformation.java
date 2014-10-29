package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.*;
import com.google.javascript.rhino.Node;

public class ASTInformation {
	private Node node;
	private int startPosition;
	private int length;
	private int nodeType;
	private volatile int hashCode = 0;

	public ASTInformation(Node node) {
		// TokenType a=node.tokenType();
		this.startPosition = node.getSourcePosition();
		this.length = node.getLength();
		this.nodeType = node.getType();
	}
}
