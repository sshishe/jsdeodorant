package ca.concordia.javascript.analysis.decomposition;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class LabelledStatement extends CompositeStatement {

	private String label;

	public LabelledStatement(ParseTree statement, CompositeStatement parent) {
		super(statement, StatementType.LABELLED, parent);

	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
