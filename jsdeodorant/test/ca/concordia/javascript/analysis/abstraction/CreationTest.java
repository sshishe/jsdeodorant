package ca.concordia.javascript.analysis.abstraction;

import org.junit.Before;
import org.junit.BeforeClass;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.SourceFile;

import ca.concordia.javascript.launcher.CLIRunner;
import ca.concordia.javascript.refactoring.RefactoringEngine;

public class CreationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIRunner runner = new CLIRunner();

		RefactoringEngine refactoringEngine = new RefactoringEngine(
				runner.createExtendedCompiler(), runner.createOptions(), null,
				null);

	}

	@Before
	public void setUp() throws Exception {

	}
}
