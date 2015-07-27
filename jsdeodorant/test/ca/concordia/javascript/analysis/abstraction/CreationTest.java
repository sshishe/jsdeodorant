package ca.concordia.javascript.analysis.abstraction;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ca.concordia.javascript.analysis.AnalysisResult;
import ca.concordia.javascript.launcher.TestRunner;

public class CreationTest {
	private static TestRunner testRunner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testRunner = new TestRunner();
	}

	@Before
	public void before() throws Exception {
		testRunner.inputs = ImmutableList.builder();
	}

	@Test
	public void testArrayWithNewKeyword() {
		try {
			testRunner.setJsFile("test/abstraction/array-creation-new-keyword.js");
			AnalysisResult result = testRunner.performActions();
			assertTrue(result.getProgram().getArrayCreationList().size() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testArrayLiteral() {
		try {
			testRunner.setJsFile("test/abstraction/array-literal.js");
			AnalysisResult result = testRunner.performActions();
			assertTrue(result.getProgram().getArrayLiteralCreationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
