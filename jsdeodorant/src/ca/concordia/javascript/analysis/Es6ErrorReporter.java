package ca.concordia.javascript.analysis;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import com.google.javascript.jscomp.parsing.parser.util.format.SimpleFormat;
import com.google.javascript.rhino.ErrorReporter;

public class Es6ErrorReporter extends
		com.google.javascript.jscomp.parsing.parser.util.ErrorReporter {
	private ErrorReporter reporter;
	private boolean errorSeen = false;
	private SourceFile source;

	Es6ErrorReporter(ErrorReporter reporter, SourceFile source, Config config) {
		this.reporter = reporter;
		this.source = source;
	}

	@Override
	protected void reportMessage(SourcePosition location, String kind,
			String format, Object... arguments) {
		String message = SimpleFormat.format("%s",
				SimpleFormat.format(format, arguments));
		switch (kind) {
		case "Error":
			if (!errorSeen) {
				errorSeen = true;
				this.reporter.error(message, location.source.name,
						location.line + 1, location.column);
			}
			break;
		case "Warning":
			this.reporter.warning(message, location.source.name,
					location.line + 1, location.column);
			break;
		default:
			throw new IllegalStateException("Unexpected:" + kind);
		}
	}

	@Override
	protected void reportMessage(SourcePosition location, String message) {
		throw new IllegalStateException("Not called directly");
	}
}
