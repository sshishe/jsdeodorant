package ca.concordia.javascript.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ca.concordia.javascript.launcher.TestRunner;

public class NamespaceTest {

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
	public void testObjectLiteralNamespace() {
		try {
			//TODO update based on recent changes
			testRunner.setJsFile("test/namespace/objectliteral.js");
			AnalysisResult result = testRunner.performActions();
			assertTrue(result.getProgram().getClassDeclarationList().size()==2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNewFunctionNamespace() {
		try {
			//TODO update based on recent changes
			testRunner.setJsFile("test/namespace/newfunction.js");
			AnalysisResult result = testRunner.performActions();
			assertTrue(result.getProgram().getClassDeclarationList().size()==1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
