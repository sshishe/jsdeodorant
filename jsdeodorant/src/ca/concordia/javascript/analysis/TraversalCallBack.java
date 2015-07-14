package ca.concordia.javascript.analysis;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.rhino.Node;

public class TraversalCallBack implements Callback {

	@Override
	public boolean shouldTraverse(NodeTraversal nodeTraversal, Node node,
			Node parent) {

		if (node.isFromExterns()) {
			return false;
		}

		if (node.isBlock())
			return true;

		if (node.isFunction()) {
			return true;
		}

		if (node.isVar()) {
			return false;
		}

		// String filename = node.getSourceFileName();

		// if (filename != null)
		// return true;
		return false;
	}

	@Override
	public void visit(NodeTraversal t, Node node, Node parent) {
		// String qualifiedName = node.getQualifiedName();
		// ControlFlowGraph<Node> cfg = t.getControlFlowGraph();

	}
}
