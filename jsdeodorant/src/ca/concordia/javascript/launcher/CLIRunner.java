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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;

public class CLIRunner extends CommandLineRunner {
	static Logger log = Logger.getLogger(CLIRunner.class.getName());
	private CompilerOptions compilerOptions;
	private static AnalysisOptions analysisOptions;
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

			CLIRunner runner = new CLIRunner();
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

		if (analysisOptions.isDisableLog())
			LogManager.getLoggerRepository().setThreshold(Level.OFF);

		addInputsFromFile(analysisOptions.getJsFiles());
		addExternsFromFile(analysisOptions.getExterns());

		RefactoringEngine refactoringEngine = new RefactoringEngine(
				createExtendedCompiler(), createOptions(), inputs.build(),
				externs.build());
		
		log.debug("analysis starts");
		refactoringEngine.run();
		log.debug("analysis ends");

		System.exit(0);
	}

	public AnalysisOptions createAnalysisOptions() throws IOException {
		analysisOptions = new AnalysisOptions();
		analysisOptions.setAdvancedAnalysis(flags.advancedAnalysis());
		analysisOptions.setOutputToCSV(flags.outputToCSV());
		analysisOptions.setCalculateCyclomatic(flags.calculateCyclomatic());
		analysisOptions.setDisableLog(flags.disableLog());
		if (!Strings.isNullOrEmpty(flags.directoryPath()))
			analysisOptions.setDirectoryPath(flags.directoryPath());
		analysisOptions.setJsFiles(flags.getJS());
		analysisOptions.setExterns(flags.getExterns());
		return analysisOptions;
	}

	@Override
	public CompilerOptions createOptions() {
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

	public CLIRunner() throws IOException {
		this(new String[0]);
	}

	protected CLIRunner(String[] args) throws IOException {
		super(args);
		compilerOptions = createOptions();
		analysisOptions = createAnalysisOptions();
	}

	public void addExternsFromFile(List<String> externs) {
		this.externs.addAll(Lists.transform(externs, TO_SOURCE_FILE_FN));
	}

	public void addInputsFromFile(List<String> inputs) {
		this.inputs.addAll(Lists.transform(inputs, TO_SOURCE_FILE_FN));
	}

	public static AnalysisOptions getAnalysisOptions() {
		return analysisOptions;
	}
}
