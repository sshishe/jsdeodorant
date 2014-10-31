package ca.concordia.javascript.analysis;

import java.io.PrintStream;

import ca.concordia.javascript.analysis.util.RhinoErrorReporter;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.rhino.ErrorReporter;

public class ExtendedCompiler extends Compiler {

	public ExtendedCompiler(PrintStream stream) {
		super(stream);
	}

	public Config getConfig(Config.LanguageMode mode) {
		return super.createConfig(mode);
	}

	public ErrorReporter getDefaultErrorReporter() {
		return RhinoErrorReporter.forOldRhino(this);
	}

}
