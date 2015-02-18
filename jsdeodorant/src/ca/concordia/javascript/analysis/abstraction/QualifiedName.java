package ca.concordia.javascript.analysis.abstraction;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class QualifiedName {
	private QualifiedName parent;
	private ParseTree node;
	private String name;

	public QualifiedName() {

	}

	public QualifiedName(String name) {
		this.name = name;
	}

	public QualifiedName(String name, QualifiedName parent) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public QualifiedName setName(String name) {
		this.name = name;
		return this;
	}

	public QualifiedName getParent() {
		return parent;
	}

	public QualifiedName createParent() {
		QualifiedName parent = new QualifiedName();
		this.parent = parent;
		return parent;
	}

	public QualifiedName createParent(String name) {
		QualifiedName parent = new QualifiedName(name);
		this.parent = parent;
		return parent;
	}

	public QualifiedName setParent(QualifiedName parent) {
		this.parent = parent;
		return this;
	}

	public ParseTree getNode() {
		return node;
	}

	public QualifiedName setNode(ParseTree node) {
		this.node = node;
		return this;
	}

	private String getQualifiedName(QualifiedName ns) {
		if (ns.parent != null)
			return getQualifiedName(ns.parent) + "." + ns.getName();
		return ns.getName();
	}

	public String toString() {
		return getQualifiedName(this);
	}
}