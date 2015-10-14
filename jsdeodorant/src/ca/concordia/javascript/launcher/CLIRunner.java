package ca.concordia.javascript.launcher;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;

import ca.concordia.javascript.analysis.AnalysisOptions;

import com.google.common.base.Strings;

public class CLIRunner extends Runner {
	private static CLIRunner runner;
	private static Flags flags;

	public CLIRunner() throws IOException {
		this(new String[0]);
	}

	protected CLIRunner(String[] args) throws IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			CLIRunner.initializeCommandLine(args);
			runner = new CLIRunner();
			runner.configureOptions();
			runner.performActions();
		} catch (CmdLineException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void configureOptions() throws IOException {
		compilerOptions = createOptions();
		setAnalysisOptions(createAnalysisOptions());
	}

	public static void initializeCommandLine(String[] args) throws CmdLineException, IOException {
		flags = new Flags();
		flags.parse(args);
	}

	@Override
	public AnalysisOptions createAnalysisOptions() {
		try {
			setAnalysisOptions(new AnalysisOptions());
			getAnalysisOptions().setClassAnalysis(flags.classAnalysis());
			getAnalysisOptions().setModuleAnlysis(flags.moduleAnalysis());
			getAnalysisOptions().setOutputToCSV(flags.outputToCSV());
			getAnalysisOptions().setCalculateCyclomatic(flags.calculateCyclomatic());
			getAnalysisOptions().setLogDisabled(flags.disableLog());
			if (!Strings.isNullOrEmpty(flags.directoryPath()))
				getAnalysisOptions().setDirectoryPath(flags.directoryPath());
			getAnalysisOptions().setJsFiles(flags.getJS());
			getAnalysisOptions().setExterns(flags.getExterns());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getAnalysisOptions();
	}
}
