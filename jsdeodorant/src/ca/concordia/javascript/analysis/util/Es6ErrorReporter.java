package ca.concordia.javascript.analysis.util;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import com.google.javascript.rhino.ErrorReporter;

public class Es6ErrorReporter extends com.google.javascript.jscomp.parsing.parser.util.ErrorReporter {
	private ErrorReporter reporter;

	public Es6ErrorReporter(ErrorReporter reporter, SourceFile source, Config config) {
		this.reporter = reporter;
	}

	@Override
	protected void reportError(SourcePosition location, String message) {
		this.reporter.error(message, location.source.name, location.line + 1, location.column);
	}

	@Override
	protected void reportWarning(SourcePosition location, String message) {
		this.reporter.warning(message, location.source.name, location.line + 1, location.column);

	}
}
