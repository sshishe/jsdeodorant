package ca.concordia.javascript.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.base.Strings;
import com.google.common.io.Files;

public class Flags {
	static Logger logger = Logger.getLogger(Flags.class.getName());
	public CmdLineParser parser = new CmdLineParser(this);

	@Option(name = "-advanced_analysis", usage = "Advanceed static analysis")
	private boolean advancedAnalysis = false;
	
	@Option(name = "-output_csv", usage = "Generate a CSV file containing analysis info")
	private boolean outputToCSV = false;

	@Option(name = "-calculate_cyclomatic", hidden = true, usage = "Enable calculation of cyclomatic complexity")
	private boolean calculateCyclomatic = false;

	@Option(name = "-directory_path", hidden = true, usage = "Directory path for javascript project")
	private String directoryPath;

	@Option(name = "-disable_log", hidden = true, usage = "Enable logging mechanism")
	private boolean disableLog = false;

	@Option(name = "-js", usage = "The JavaScript filenames, From Google Closure Flags class")
	private List<String> js = new ArrayList<>();

	@Option(name = "-externs", usage = "List of externs files to use in the compilation.")
	private List<String> externs = new ArrayList<>();

	public boolean advancedAnalysis() {
		return advancedAnalysis;
	}

	public boolean outputToCSV() {
		return outputToCSV;
	}
	
	public boolean disableLog() {
		return disableLog;
	}
	
	public String directoryPath(){
		return directoryPath;
	}

	public boolean calculateCyclomatic() {
		return calculateCyclomatic;
	}

	public List<String> getJS() throws IOException {
		List<String> filesInDirectory = getFilesInDirectory();
		if (filesInDirectory != null)
			js.addAll(filesInDirectory);

		if (js.isEmpty())
			throw new IOException(
					"Expected input file(s) either using -js or -directory_path");
		return js;
	}

	private List<String> getFilesInDirectory() throws FileNotFoundException {
		List<String> jsFiles = new ArrayList<>();
		if (!Strings.isNullOrEmpty(directoryPath)) {
			File rootDir = new File(directoryPath);

			if (!rootDir.exists())
				throw new FileNotFoundException(
						"The directory path is not valid");

			for (File f : Files.fileTreeTraverser().preOrderTraversal(rootDir)) {
				if (f.isFile()
						&& Files.getFileExtension(f.toPath().toString())
								.toLowerCase().equals("js"))
					jsFiles.add(f.toPath().toString());
			}
			return jsFiles;
		}
		return null;
	}

	public List<String> getExterns() {
		return externs;
	}

	public void parse(String[] args) throws CmdLineException {
		parser.parseArgument(args);
	}
}