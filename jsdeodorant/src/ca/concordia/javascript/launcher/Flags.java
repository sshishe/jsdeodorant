package ca.concordia.javascript.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import ca.concordia.javascript.analysis.util.FileUtil;

public class Flags {
	static Logger logger = Logger.getLogger(Flags.class.getName());
	public CmdLineParser parser = new CmdLineParser(this);

	@Option(name = "-class_analysis", usage = "Advanceed static analysis")
	private boolean classAnalysis = false;

	@Option(name = "-output_csv", usage = "Generate a CSV file containing analysis info")
	private boolean outputToCSV = false;

	@Option(name = "-calculate_cyclomatic", hidden = true, usage = "Enable calculation of cyclomatic complexity")
	private boolean calculateCyclomatic = false;

	@Option(name = "-module-analysis", hidden = true, usage = "Enable module analysis for Node style packaging")
	private boolean moduleAnalysis = false;

	@Option(name = "-directory_path", hidden = true, usage = "Directory path for javascript project")
	private String directoryPath;

	@Option(name = "-disable_log", hidden = true, usage = "Enable logging mechanism")
	private boolean disableLog = false;

	@Option(name = "-js", usage = "The JavaScript filenames, From Google Closure Flags class")
	private List<String> js = new ArrayList<>();

	@Option(name = "-externs", usage = "List of externs files to use in the compilation.")
	private List<String> externs = new ArrayList<>();

	public boolean classAnalysis() {
		return classAnalysis;
	}

	public boolean moduleAnalysis() {
		return moduleAnalysis;
	}

	public boolean outputToCSV() {
		return outputToCSV;
	}

	public boolean disableLog() {
		return disableLog;
	}

	public String directoryPath() {
		return directoryPath;
	}

	public boolean calculateCyclomatic() {
		return calculateCyclomatic;
	}

	public List<String> getJS() throws IOException {
		List<String> filesInDirectory = getFilesInDirectory();
		if (filesInDirectory != null)
			js.addAll(filesInDirectory);
		return js;
	}

	private List<String> getFilesInDirectory() throws FileNotFoundException {
		return FileUtil.getFilesInDirectory(directoryPath);
	}

	public List<String> getExterns() {
		return externs;
	}

	public void parse(String[] args) throws CmdLineException {
		parser.parseArgument(args);
	}
}