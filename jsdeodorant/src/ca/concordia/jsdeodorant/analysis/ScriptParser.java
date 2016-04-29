package ca.concordia.jsdeodorant.analysis;

import java.io.IOException;
import java.util.List;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

import ca.concordia.jsdeodorant.analysis.util.Es6ErrorReporter;

import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.jscomp.parsing.parser.Parser;
import com.google.javascript.jscomp.parsing.parser.trees.*;

public class ScriptParser {
	private final ExtendedCompiler compiler;

	private List<String> messages;

	public List<String> getMessages() {
		return messages;
	}

	public ScriptParser(ExtendedCompiler compiler) {
		this.compiler = compiler;
	}

	public List<String> analyze() {
		TraversalCallBack traversalCallBack = new TraversalCallBack();

		Node rootNode = compiler.getRoot();

		NodeTraversal.traverse(compiler, rootNode, traversalCallBack);
		return messages;
	}

	public ProgramTree parse(SourceFile sourceFile) {
		try {
			com.google.javascript.jscomp.parsing.parser.SourceFile sourceFileForParser = new com.google.javascript.jscomp.parsing.parser.SourceFile(sourceFile.getName(), sourceFile.getCode());

			com.google.javascript.jscomp.parsing.Config configForErrorReporter = compiler.getConfig(LanguageMode.ECMASCRIPT6);

			Es6ErrorReporter es6ErrorReporter = new Es6ErrorReporter(compiler.getDefaultErrorReporter(), sourceFile, configForErrorReporter);
			com.google.javascript.jscomp.parsing.parser.Parser.Config es6config = new com.google.javascript.jscomp.parsing.parser.Parser.Config(mode(LanguageMode.ECMASCRIPT6));

			Parser parser = new Parser(es6config, es6ErrorReporter, sourceFileForParser);

			return parser.parseProgram();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode mode(com.google.javascript.jscomp.parsing.Config.LanguageMode mode) {
		switch (mode) {
		case ECMASCRIPT3:
			return com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode.ES3;
		case ECMASCRIPT5:
			return com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode.ES5;
		case ECMASCRIPT5_STRICT:
			return com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode.ES5_STRICT;
		case ECMASCRIPT6:
			return com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode.ES6;
		case ECMASCRIPT6_STRICT:
			return com.google.javascript.jscomp.parsing.parser.Parser.Config.Mode.ES6_STRICT;
		default:
			throw new IllegalStateException("unexpected");
		}
	}

}
