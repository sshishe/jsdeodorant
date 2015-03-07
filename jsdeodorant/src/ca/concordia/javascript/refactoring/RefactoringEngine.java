package ca.concordia.javascript.refactoring;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.analysis.ScriptParser;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.util.CompositePostProcessor;
import ca.concordia.javascript.analysis.util.ExperimentOutput;
import ca.concordia.javascript.metrics.CyclomaticComplexity;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;

public class RefactoringEngine {
	static Logger log = Logger.getLogger(RefactoringEngine.class.getName());
	private Program program;
	private final ExtendedCompiler compiler;
	private final CompilerOptions compilerOptions;
	private static ImmutableList<SourceFile> inputs;
	private static ImmutableList<SourceFile> externs;

	public RefactoringEngine(ExtendedCompiler compiler,
			CompilerOptions compilerOptions, ImmutableList<SourceFile> inputs,
			ImmutableList<SourceFile> externs) {
		this.compiler = compiler;

		this.compilerOptions = compilerOptions;
		this.compilerOptions.setIdeMode(false);
		CompilationLevel.WHITESPACE_ONLY
				.setOptionsForCompilationLevel(this.compilerOptions);
		WarningLevel warningLevel = WarningLevel.QUIET;
		warningLevel.setOptionsForWarningLevel(this.compilerOptions);

		RefactoringEngine.inputs = inputs;
		RefactoringEngine.externs = externs;
	}

	public List<String> run() {
		compiler.compile(externs, inputs, compilerOptions);
		ScriptParser scriptAnalyzer = new ScriptParser(compiler);
		program = new Program();

		for (SourceFile sourceFile : inputs) {
			ProgramTree programTree = scriptAnalyzer.parse(sourceFile);
			for (ParseTree sourceElement : programTree.sourceElements) {
				StatementProcessor.processStatement(sourceElement, program);
			}
		}
		CompositePostProcessor.processFunctionDeclarations(program);
		CyclomaticComplexity cyclomaticComplexity = new CyclomaticComplexity(
				program);

		for (Map.Entry<String, Integer> entry : cyclomaticComplexity
				.calculate().entrySet()) {
			System.out.println("Cyclomatic Complexity of " + entry.getKey()
					+ " is: " + entry.getValue());
		}

		ExperimentOutput experimentOutput = new ExperimentOutput(program);
		experimentOutput.writeToFile();
		experimentOutput.uniqueClassDeclarationNumber();

		return scriptAnalyzer.getMessages();
	}

	public Program getProgram() {
		return program;
	}
}
