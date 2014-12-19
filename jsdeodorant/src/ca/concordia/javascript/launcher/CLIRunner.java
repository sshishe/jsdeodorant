package ca.concordia.javascript.launcher;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;

import ca.concordia.javascript.analysis.ExtendedCompiler;
import ca.concordia.javascript.refactoring.RefactoringEngine;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;

public class CLIRunner extends CommandLineRunner {
	static Logger log = Logger.getLogger(CLIRunner.class.getName());
	private CompilerOptions compilerOptions;
	private static Flags flags;
	private final ImmutableList.Builder<SourceFile> inputs = ImmutableList
			.builder();
	private final ImmutableList.Builder<SourceFile> externs = ImmutableList
			.builder();

	private static final Function<String, SourceFile> TO_SOURCE_FILE_FN = new Function<String, SourceFile>() {
		@Override
		public SourceFile apply(String file) {
			return new SourceFile.Builder().buildFromFile(file);
		}
	};

	public static void main(String[] args) {
		try {
			CLIRunner.initializeCommandLine(args);

			// instantiate CLIRRunner with no argument
			CLIRunner runner = new CLIRunner(new String[0]);

			runner.performActions();

		} catch (CmdLineException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void initializeCommandLine(String[] args)
			throws CmdLineException, IOException {
		flags = new Flags();
		flags.parse(args);
	}

	private void performActions() throws IOException {
		externs.addAll(ImmutableList.<SourceFile> of());

		if (flags.disableLog)
			LogManager.getLoggerRepository().setThreshold(Level.OFF);

		addInputsFromFile(flags.getJS());
		addExternsFromFile(flags.getExterns());

		RefactoringEngine refactoringEngine = new RefactoringEngine(
				createExtendedCompiler(), createOptions(), inputs.build(),
				externs.build());
		log.debug("analysis starts");
		refactoringEngine.run();
		log.debug("analysis ends");
		if (flags.hasPrintModel())
			printModel();

		if (flags.hasPrintFlowGraph())
			printflowGraph();

		if (flags.hasAdvancedAnalysis()) {
			// Program program =
			// System.out.println("Object Creations using new keyword:"
			// + program.getObjectCreations().size());
			// System.out.println("Array Creations using new keyword:"
			// + program.getArrayCreations().size());
			// System.out.println("Array Literal Creations: "
			// + program.getArrayLiteralCreations().size());
			// System.out.println("Object Literal Creations: "
			// + program.getObjectLiteralCreations().size());

			// for (Creation creation : program.getObjectCreations()) {
			// if (creation instanceof ObjectCreation)
			// System.out.println("Object Creation");
			// if (creation instanceof ArrayLiteralCreation)
			// System.out.println("Array Literal Creation");
			// if (creation instanceof ObjectLiteralCreation)
			// System.out.println("Object Literal Creation");
			// }
		}

		System.exit(0);
	}

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

	public void addExternsFromFile(List<String> externs) {
		this.externs.addAll(Lists.transform(externs, TO_SOURCE_FILE_FN));
	}

	public void addInputsFromFile(List<String> inputs) {
		this.inputs.addAll(Lists.transform(inputs, TO_SOURCE_FILE_FN));
	}

	private static void printModel() {
		log.info("Printing Model");
	}

	private static void printflowGraph() {
		log.info("Printing Flow Graph");
	}

}
