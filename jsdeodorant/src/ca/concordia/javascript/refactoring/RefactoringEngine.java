package ca.concordia.javascript.refactoring;

import java.util.List;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.analysis.ScriptParser;
import ca.concordia.javascript.analysis.abstraction.FunctionDeclaration;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.decomposition.FunctionBody;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
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

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();

		ProgramTree programTree = scriptAnalyzer.parse(inputs.get(0));

		List<ParseTree> literalExpressions = expressionExtractor
				.getLiteralExpressions(programTree);

		List<ParseTree> variableDeclarations = expressionExtractor
				.getVariableDeclarationExpressions(programTree);

		List<ParseTree> identifiers = expressionExtractor
				.getIdentifierExpressions(programTree);

		List<ParseTree> callExpressions = expressionExtractor
				.getCallExpressions(programTree);

		List<ParseTree> objectLiteralExpressions = expressionExtractor
				.getObjectLiteralExpressions(programTree);

		List<ParseTree> newExpressions = expressionExtractor
				.getNewExpressions(programTree);

		List<ParseTree> assignmentRestExpressions = expressionExtractor
				.getAssignmentRestExpressions(programTree);

		List<ParseTree> binaryOperators = expressionExtractor
				.getBinaryOperators(programTree);

		List<ParseTree> postfixExpressions = expressionExtractor
				.getPostfixExpressions(programTree);
		
		List<ParseTree> arrayPattern = expressionExtractor
				.getArrayPatterns(programTree);
		
		List<ParseTree> commaExpressions = expressionExtractor
				.getCommaExpressions(programTree);
		

		List<ParseTree> arrayLiteralExpressions = expressionExtractor
				.getArrayLiteralExpressions(programTree);

		Program program = new Program();
		for (ParseTree sourceElement : programTree.sourceElements) {
			if (sourceElement instanceof FunctionDeclarationTree) {
				FunctionDeclarationTree functionDeclaration = sourceElement
						.asFunctionDeclaration();

				program.addSourceElement(processFunctionDeclaration(functionDeclaration));
			}
		}

		scriptAnalyzer.analyze();
		return scriptAnalyzer.getMessages();
	}

	private FunctionDeclaration processFunctionDeclaration(
			FunctionDeclarationTree functionDeclarationTree) {
		FunctionDeclaration functionDeclaration = new FunctionDeclaration();
		functionDeclaration.setName(functionDeclarationTree.name.value);

		ParseTree functionBodyTree = functionDeclarationTree.functionBody;

		if (functionBodyTree instanceof BlockTree) {
			BlockTree blockTree = functionBodyTree.asBlock();
			FunctionBody functionBody = new FunctionBody(blockTree);
			functionDeclaration.setBody(functionBody);
		}

		// If the body is not BlockTree it will be an expression
		else {

		}

		return functionDeclaration;
	}
}
