package ca.concordia.javascript.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.base.Strings;

import com.google.common.io.Files;

public class Flags {
	public CmdLineParser parser = new CmdLineParser(this);

	@Option(name = "-advanced_analysis", usage = "Advanceed static analysis")
	private boolean advancedAnalysis = false;

	@Option(name = "-print_model", usage = "Prints abstract model")
	private boolean printModel = false;

	@Option(name = "-print_flowgraph", usage = "Prints flow graph")
	private boolean printFlowGraph = false;

	@Option(name = "-directory_path", hidden = true, usage = "Directory path for javascript project")
	public String directoryPath;

	@Option(name = "-js", usage = "The JavaScript filenames, From Google Closure Flags class")
	private List<String> js = new ArrayList<>();

	@Option(name = "-externs", usage = "List of externs files to use in the compilation.")
	private List<String> externs = new ArrayList<>();

	public boolean hasAdvancedAnalysis() {
		return advancedAnalysis;
	}

	public boolean hasPrintModel() {
		return printModel;
	}

	public boolean hasPrintFlowGraph() {
		return printFlowGraph;
	}

	public List<String> getJS() {
		js.addAll(getFilesInDirectory());
		return js;
	}

	private List<String> getFilesInDirectory() {
		if (!Strings.isNullOrEmpty(directoryPath)) {
			List<String> jsFiles = new ArrayList<>();
			File rootDir = new File(directoryPath);
			for (File f : Files.fileTreeTraverser().preOrderTraversal(rootDir)) {
				if (f.isFile()
						&& Files.getFileExtension(f.toPath().toString())
								.toLowerCase().equals("js"))
					jsFiles.add(f.toPath().toString());
			}
			return jsFiles;
		}
		return new ArrayList<String>();
	}

	public List<String> getExterns() {
		return externs;
	}

	public void parse(String[] args) {
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
	}
}