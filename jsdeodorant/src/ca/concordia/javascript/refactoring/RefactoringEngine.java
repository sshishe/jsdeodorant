package ca.concordia.javascript.refactoring;

import java.util.List;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.analysis.ScriptAnalyzer;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;

public class RefactoringEngine {
	private final ExtendedCompiler compiler;
	private final CompilerOptions compilerOptions;
	private final ImmutableList<SourceFile> inputs;
	private final ImmutableList<SourceFile> externs;

	public RefactoringEngine(ExtendedCompiler compiler,
			CompilerOptions compilerOptions, ImmutableList<SourceFile> inputs,
			ImmutableList<SourceFile> externs) {

		this.compiler = compiler;

		this.compilerOptions = compilerOptions;
		this.compilerOptions.setIdeMode(false);
		CompilationLevel.WHITESPACE_ONLY
				.setOptionsForCompilationLevel(this.compilerOptions);

		this.inputs = inputs;
		this.externs = externs;

	}

	public List<String> run() {
		compiler.compile(externs, inputs, compilerOptions);
		ScriptAnalyzer scriptAnalyzer = new ScriptAnalyzer(compiler);
		ProgramTree tree = scriptAnalyzer.parse(inputs.get(0));

		scriptAnalyzer.analyze();
		return scriptAnalyzer.getMessages();
	}
}
