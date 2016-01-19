package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.parsing.parser.util.SourceRange;

public class SourceLocationHelper {
	public static String getLocation(SourceRange sourceRange) {
		StringBuffer loc = new StringBuffer();
		loc.append("(");
		loc.append(sourceRange.start.line);
		loc.append(",");
		loc.append(sourceRange.start.column);
		loc.append(")");
		loc.append("-");
		loc.append("(");
		loc.append(sourceRange.end.line);
		loc.append(",");
		loc.append(sourceRange.end.column);
		loc.append(")");
		return loc.toString();
	}
}
