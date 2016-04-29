package ca.concordia.jsdeodorant.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;

public class LabelledStatement extends CompositeStatement {

	private String label;

	public LabelledStatement(ParseTree statement, SourceContainer parent) {
		super(statement, StatementType.LABELLED, parent);

	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
