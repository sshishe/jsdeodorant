package ca.concordia.javascript.launcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.spi.OptionHandler;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.refactoring.RefactoringEngine;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;

public class CLIRunner extends CommandLineRunner {
	private static final Function<String, SourceFile> TO_SOURCE_FILE_FN = new Function<String, SourceFile>() {
		@Override
		public SourceFile apply(String file) {
			return new SourceFile.Builder().buildFromFile(file);
		}
	};

	public static void main(String[] args) {
		try {
			CLIRunner.initializeCommandLine(args);
		} catch (CmdLineException | IOException e) {
			e.printStackTrace();
		}
	}

	private CompilerOptions compilerOptions;
	private static Flags flags;
	private final ImmutableList.Builder<SourceFile> inputs = ImmutableList
			.builder();
	private final ImmutableList.Builder<SourceFile> externs = ImmutableList
			.builder();

	@Override
	protected CompilerOptions createOptions() {
		compilerOptions = super.createOptions();
		return compilerOptions;
	}

	@Override
	protected Compiler createCompiler() {
		Compiler compiler = super.createCompiler();
		return compiler;
	}

	public ExtendedCompiler createExtendedCompiler() {
		return new ExtendedCompiler(getErrorPrintStream());
	}

	protected CLIRunner(String[] args) {
		super(args);
		compilerOptions = createOptions();
	}

	public static void initializeCommandLine(String[] args)
			throws CmdLineException, IOException {
		flags = new Flags();
		flags.parse(args);

		// instantiate CLIRRunner with no argument
		CLIRunner runner = new CLIRunner(new String[0]);

		runner.performParsingCommandLine();
	}

	private void performParsingCommandLine() throws IOException {
		externs.addAll(ImmutableList.<SourceFile> of());

		addInputsFromFile(flags.getJS());
		addExternsFromFile(flags.getExterns());

		if (flags.hasPrintModel())
			printModel();

		if (flags.hasPrintFlowGraph())
			printflowGraph();

		if (flags.hasAdvancedAnalysis()) {
			RefactoringEngine refactoringEngine = new RefactoringEngine(
					createExtendedCompiler(), createOptions(), inputs.build(),
					externs.build());
			refactoringEngine.run();
		}
		System.exit(0);
	}

	public void addExternsFromFile(List<String> externs) {
		this.externs.addAll(Lists.transform(externs, TO_SOURCE_FILE_FN));
	}

	public void addInputsFromFile(List<String> inputs) {
		this.inputs.addAll(Lists.transform(inputs, TO_SOURCE_FILE_FN));
	}

	private static void printModel() {
		System.out.println("Printing Model");
	}

	private static void printflowGraph() {
		System.out.println("Printing Flow Graph");
	}

}
