package ca.concordia.javascript.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;

import ca.concordia.javascript.analysis.AnalysisOptions;
import ca.concordia.javascript.analysis.util.FileUtil;

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
			runToolForInnerModules(runner, flags.directoryPath() + "/node_modules");
		} catch (CmdLineException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static void runToolForInnerModules(CLIRunner runner, String nodeModuleFolder) throws IOException {
		//runner.inputs
		List<String> modules = FileUtil.getDirectoriesInDirectory(nodeModuleFolder);
		if (modules == null)
			return;
		for (final String innerModulePath : modules) {
			if (innerModulePath.contains(".bin"))
				continue;
			getAnalysisOptions().setDirectoryPath(innerModulePath);
			flags.clearJS();
			flags.setDirectoryPath(innerModulePath);
			flags.setLibraries(new ArrayList<String>() {
				{
					add(innerModulePath + File.separator + "node_modules");
					add(innerModulePath + File.separator + "lib");
				}
			});
			runner.configureOptions();
			runner.performActions();
			runToolForInnerModules(runner, innerModulePath + File.separator + "node_modules");
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
			getAnalysisOptions().setFunctionAnalysis(flags.functionAnalysis());
			getAnalysisOptions().setModuleAnlysis(flags.moduleAnalysis());
			getAnalysisOptions().setOutputToCSV(flags.outputToCSV());
			getAnalysisOptions().setOutputToDB(flags.outputToDB());
			getAnalysisOptions().setCalculateCyclomatic(flags.calculateCyclomatic());
			getAnalysisOptions().setLogDisabled(flags.disableLog());
			if (!Strings.isNullOrEmpty(flags.directoryPath()))
				getAnalysisOptions().setDirectoryPath(flags.directoryPath());
			getAnalysisOptions().setJsFiles(flags.getJS());
			getAnalysisOptions().setExterns(flags.getExterns());
			getAnalysisOptions().setLibraries(flags.getLibraries());
			getAnalysisOptions().setAnalyzeLibrariesForClasses(flags.analyzeLibraryClasses());
			//getAnalysisOptions().setLibrariesWithPath(flags.getLibrariesWithPath());
			getAnalysisOptions().setBuiltinLibraries(flags.getBuiltinLibraries());
			getAnalysisOptions().setPsqlServerName(flags.getPsqlServerName());
			getAnalysisOptions().setPsqlPortNumber(flags.getPsqlPort());
			getAnalysisOptions().setPsqlDatabaseName(flags.getPsqlDbName());
			getAnalysisOptions().setPsqlUser(flags.getPsqlUser());
			getAnalysisOptions().setPsqlPassword(flags.getPsqlPassword());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getAnalysisOptions();
	}
}
