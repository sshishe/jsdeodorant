package ca.concordia.javascript.refactoring;

import java.util.List;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.analysis.ScriptParser;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
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
		ScriptParser scriptAnalyzer = new ScriptParser(compiler);
		Program program = new Program();

		for (SourceFile sourceFile : inputs) {
			ProgramTree programTree = scriptAnalyzer.parse(sourceFile);
			for (ParseTree sourceElement : programTree.sourceElements) {

				// ExpressionExtractor expressionExtractor = new
				// ExpressionExtractor();
				// List<ParseTree> literalExpressions = expressionExtractor
				// .getLiteralExpressions(programTree);
				//
				// List<ParseTree> variableDeclarations = expressionExtractor
				// .getVariableDeclarationExpressions(programTree);
				//
				// List<ParseTree> identifiers = expressionExtractor
				// .getIdentifierExpressions(programTree);
				//
				// List<ParseTree> callExpressions = expressionExtractor
				// .getCallExpressions(programTree);
				//
				// List<ParseTree> objectLiteralExpressions =
				// expressionExtractor
				// .getObjectLiteralExpressions(programTree);
				//
				// List<ParseTree> newExpressions = expressionExtractor
				// .getNewExpressions(programTree);
				//
				// List<ParseTree> assignmentRestExpressions =
				// expressionExtractor
				// .getAssignmentRestExpressions(programTree);
				//
				// List<ParseTree> binaryOperators = expressionExtractor
				// .getBinaryOperators(programTree);
				//
				// List<ParseTree> postfixExpressions = expressionExtractor
				// .getPostfixExpressions(programTree);
				//
				// List<ParseTree> arrayPattern = expressionExtractor
				// .getArrayPatterns(programTree);
				//
				// List<ParseTree> commaExpressions = expressionExtractor
				// .getCommaExpressions(programTree);
				//
				// List<ParseTree> arrayLiteralExpressions = expressionExtractor
				// .getArrayLiteralExpressions(programTree);
				//
				// List<ParseTree> functionDeclarations = expressionExtractor
				// .getFunctionDeclarations(programTree);

				if (sourceElement instanceof FunctionDeclarationTree) {
					FunctionDeclarationTree functionDeclaration = sourceElement
							.asFunctionDeclaration();
					program.addSourceElement(AbstractFunctionFragment
							.processFunctionDeclaration(functionDeclaration));
				} else if (sourceElement instanceof ParseTree) {
					StatementProcessor.processStatement(sourceElement, program);
				}
			}
		}

		return scriptAnalyzer.getMessages();
	}

}
