package ca.concordia.javascript.analysis.decomposition;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

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
