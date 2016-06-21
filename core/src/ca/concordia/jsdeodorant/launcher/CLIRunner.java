package ca.concordia.jsdeodorant.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.common.base.Strings;

import ca.concordia.jsdeodorant.analysis.AnalysisOptions;
import ca.concordia.jsdeodorant.analysis.util.FileUtil;

public class CLIRunner extends Runner {
	private static CLIRunner runner;
	private static Flags flags;
	private static CmdLineParser parser;

	public CLIRunner() throws IOException {
		this(new String[0]);
	}

	protected CLIRunner(String[] args) throws IOException {
		super(args);
	}

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		try {
			CLIRunner.initializeCommandLine(args);
			if (flags.getHelp()) {
				parser.printUsage(System.err);
				return;
			}
			runner = new CLIRunner();
			runner.configureOptions();
			runner.performActions();
			runToolForInnerModules(runner, flags.directoryPath() + "/node_modules");
		} catch (CmdLineException | IOException e) {
			parser.printUsage(System.err);
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
		parser = flags.getParser();
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
			getAnalysisOptions().setName(flags.getName());
			getAnalysisOptions().setVersion(flags.getVersion());
			getAnalysisOptions().setPackageSystem(flags.getPackageSystem());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getAnalysisOptions();
	}
}
