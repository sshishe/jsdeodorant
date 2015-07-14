package ca.concordia.javascript.analysis;

import java.util.Map;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
import ca.concordia.javascript.analysis.util.ExperimentOutput;
import ca.concordia.javascript.metrics.CyclomaticComplexity;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;

public class AnalysisEngine {
	static Logger log = Logger.getLogger(AnalysisEngine.class.getName());
	private Program program;
	private final ExtendedCompiler compiler;
	private final CompilerOptions compilerOptions;
	private ImmutableList<SourceFile> inputs;
	private ImmutableList<SourceFile> externs;

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions) {
		this(compiler, compilerOptions, null, null);
	}

	public AnalysisEngine(ExtendedCompiler compiler, CompilerOptions compilerOptions, ImmutableList<SourceFile> inputs, ImmutableList<SourceFile> externs) {
		this.compiler = compiler;

		this.compilerOptions = compilerOptions;
		this.compilerOptions.setIdeMode(false);
		CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(this.compilerOptions);
		WarningLevel warningLevel = WarningLevel.QUIET;
		warningLevel.setOptionsForWarningLevel(this.compilerOptions);

		this.inputs = inputs;
		this.externs = externs;
	}

	public AnalysisResult run(AnalysisOptions analysisOption) {
		compiler.compile(externs, inputs, compilerOptions);
		ScriptParser scriptAnalyzer = new ScriptParser(compiler);
		program = new Program();

		for (SourceFile sourceFile : inputs) {
			ProgramTree programTree = scriptAnalyzer.parse(sourceFile);
			for (ParseTree sourceElement : programTree.sourceElements) {
				StatementProcessor.processStatement(sourceElement, program);
			}
		}

		for (FunctionDeclaration functionDeclaration : program.getFunctionDeclarationList()) {
			if (functionDeclaration instanceof FunctionDeclarationExpression) {
				FunctionDeclarationExpression functionDeclarationExpression = (FunctionDeclarationExpression) functionDeclaration;
				if (functionDeclarationExpression.hasNamespace())
					log.warn(functionDeclarationExpression.getNamespace() + "." + functionDeclaration.getName());
				else
					log.warn(functionDeclarationExpression.getName());
			}
		}

		if (analysisOption.isAdvancedAnalysis())
			CompositePostProcessor.processFunctionDeclarations(program);

		if (analysisOption.isCalculateCyclomatic()) {
			CyclomaticComplexity cyclomaticComplexity = new CyclomaticComplexity(program);

			// OOPMetrics oopMetrics = new OOPMetrics(program);
			for (Map.Entry<String, Integer> entry : cyclomaticComplexity.calculate().entrySet()) {
				log.warn("Cyclomatic Complexity of " + entry.getKey() + " is: " + entry.getValue());
			}
		}
		if (analysisOption.isOutputToCSV()) {
			ExperimentOutput experimentOutput = new ExperimentOutput(program);
			experimentOutput.writeToFile();
			experimentOutput.uniqueClassDeclarationNumber();
		}

		return new AnalysisResult(program, scriptAnalyzer.getMessages());
	}

	public Program getProgram() {
		return program;
	}

	public ImmutableList<SourceFile> getInputs() {
		return this.inputs;
	}

	public void setInputs(ImmutableList<SourceFile> inputs) {
		this.inputs = inputs;
	}
}
