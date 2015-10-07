package ca.concordia.javascript.launcher;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.AnalysisEngine;
import ca.concordia.javascript.analysis.AnalysisInstance;
import ca.concordia.javascript.analysis.AnalysisOptions;
import ca.concordia.javascript.analysis.ExtendedCompiler;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;

public abstract class Runner extends CommandLineRunner {
	protected static Logger log = Logger.getLogger(AnalysisEngine.class.getName());
	protected CompilerOptions compilerOptions;
	private static AnalysisOptions analysisOptions;
	public ImmutableList.Builder<SourceFile> inputs = ImmutableList.builder();
	public ImmutableList.Builder<SourceFile> externs = ImmutableList.builder();

	public static final Function<String, SourceFile> TO_SOURCE_FILE_FN = new Function<String, SourceFile>() {
		@Override
		public SourceFile apply(String file) {
			return new SourceFile.Builder().buildFromFile(file);
		}
	};

	protected Runner(String[] args) {
		super(args);
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

	public void addExternsFromFile(List<String> externs) {
		this.externs.addAll(Lists.transform(externs, TO_SOURCE_FILE_FN));
	}

	public void addInputsFromFile(List<String> inputs) {
		this.inputs.addAll(Lists.transform(inputs, TO_SOURCE_FILE_FN));
	}

	public ExtendedCompiler createExtendedCompiler() {
		return new ExtendedCompiler(getErrorPrintStream());
	}

	public List<AnalysisInstance> performActions() throws IOException {
		externs.addAll(ImmutableList.<SourceFile> of());
		if (analysisOptions.isLogDisabled())
			LogManager.getLoggerRepository().setThreshold(Level.OFF);
		addInputsFromFile(analysisOptions.getJsFiles());
		addExternsFromFile(analysisOptions.getExterns());
		AnalysisEngine analysisEngine = new AnalysisEngine(createExtendedCompiler(), createOptions(), inputs.build(), externs.build());
		log.debug("analysis starts");
		List<AnalysisInstance> results = analysisEngine.run(analysisOptions);
		log.debug("analysis ends");
		return results;
	}

	public abstract AnalysisOptions createAnalysisOptions();

	public static AnalysisOptions getAnalysisOptions() {
		return analysisOptions;
	}

	public static void setAnalysisOptions(AnalysisOptions analysisOptions) {
		Runner.analysisOptions = analysisOptions;
	}

}
