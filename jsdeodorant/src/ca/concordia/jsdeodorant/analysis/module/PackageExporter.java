package ca.concordia.jsdeodorant.analysis.module;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public interface PackageExporter {
	void extract(ParseTree expression);
}
