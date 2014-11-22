package ca.concordia.javascript.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.OptionHandler;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Flags {
	public CmdLineParser parser = new CmdLineParser(this);

	@Option(name = "-advanced_analysis", hidden = true, usage = "Advanceed static analysis")
	private boolean advancedAnalysis = false;

	@Option(name = "-print_model", hidden = true, usage = "Prints abstract model")
	private boolean printModel = false;

	@Option(name = "-directory", usage = "Directory path for javascript project")
	private void setDirectory(String path) {
		System.out.print(path);
	}
	
	@Option(name = "-print_flowgraph", hidden = true, usage = "Prints flow graph")
	private boolean printFlowGraph = false;



	//@Option(name = "--js", usage = "The JavaScript filenames, From Google Closure Flags class")
	private List<String> js = new ArrayList<>();

	//@Option(name = "--externs", usage = "List of externs files to use in the compilation.")
	private List<String> externs = new ArrayList<>();

	/*
	 * Idea from RefasterJS.Java in Closure Compiler
	 */
	@Option(name = "--include_default_externs", usage = "Whether to include the standard JavaScript externs. Defaults to true.")
	private boolean includeDefaultExterns = true;

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
		// if (!Strings.isNullOrEmpty(inputDirectory)) {
		// List<String> jsFiles = new ArrayList<>();
		// File rootDir = new File(inputDirectory);
		// for (File f : Files.fileTreeTraverser().preOrderTraversal(rootDir)) {
		// jsFiles.add(f.toPath().toString());
		// System.out.println(f.toPath());
		//
		// // if you need the relative path, with respect to rootDir
		// // Path relativePath = rootDir.toPath().getParent()
		// // .relativize(f.toPath());
		// }
		// }
		return new ArrayList<String>();
	}

	public List<String> getExterns() {
		return externs;
	}

	public boolean getIncludeDefaultExterns() {
		return includeDefaultExterns;
	}

	public void parse(String[] args) {
		List<String> tokens = processSpecificClosureArgs(args);
		try {

			parser.parseArgument(tokens);
		} catch (CmdLineException e) {
			// Happens because of Closure commands
		}

	}

	private static List<String> processSpecificClosureArgs(String[] args) {
		Pattern specificClosureArgs = Pattern
				.compile("(-\\S+( )*\\S+(.js)*?$|\\S+(.js)$)");

		List<String> processedArgs = new ArrayList<>();

		for (String arg : args) {
			Matcher matcher = specificClosureArgs.matcher(arg);
			if (matcher.find()) {
				processedArgs.add(arg);
			}
		}
		return processedArgs;
	}

	public List<String> getJSDeodorantOptionNames() {
		List<String> jsDeodorantOptionNames = new ArrayList<String>();
		for (@SuppressWarnings("rawtypes")
		OptionHandler opt : this.parser.getOptions()) {
			if (!opt.option.toString().contains("--"))
				jsDeodorantOptionNames.add(opt.option.toString());
		}
		return jsDeodorantOptionNames;
	}

	public List<String> getClosureArgs(String[] args) {
		List<String> closureArgs = new ArrayList<String>();
		closureArgs.addAll(Collections2.filter((Arrays.asList(args)),
				Predicates.not(Predicates.in(Lists
						.newArrayList(getJSDeodorantOptionNames())))));

		return closureArgs;
	}

}