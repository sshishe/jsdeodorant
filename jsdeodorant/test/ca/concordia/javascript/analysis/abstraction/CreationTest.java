package ca.concordia.javascript.analysis.abstraction;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

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
		testRunner.setJsFile("test/abstraction/array-creation-new-keyword.js");
		Module result = testRunner.performActionsForModule();
		assertTrue(result.getProgram().getArrayCreationList().size() == 1);
	}

	@Test
	public void testArrayLiteral() {
		testRunner.setJsFile("test/abstraction/array-literal.js");
		Module result = testRunner.performActionsForModule();
		assertTrue(result.getProgram().getArrayLiteralCreationList().size() == 2);
	}
}
