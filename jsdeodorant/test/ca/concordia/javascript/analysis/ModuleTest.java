package ca.concordia.javascript.analysis;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.util.FileUtil;
import ca.concordia.javascript.launcher.TestRunner;

public class ModuleTest {
	private static TestRunner testRunner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testRunner = new TestRunner();
	}

	@Before
	public void before() throws Exception {
		testRunner.inputs = ImmutableList.builder();
	}

	private List<Module> setAnalysisForCommonJSModuleTest() throws IOException {
		testRunner.setDirectoryPath("test/module/CommonJS/");
		List<Module> result = testRunner.performActionsForModules();
		return result;
	}

	@Test
	public void testCrossClassDetectionForCJS() {
		try {
			List<Module> modules = setAnalysisForCommonJSModuleTest();
			for (Module module : modules) {
				String[] fileNameParts = module.getSourceFile().getName().split("/");
				if (FileUtil.getElementsOf(fileNameParts, fileNameParts.length - 1, fileNameParts.length - 1).equals("usage.js"))
					for (ObjectCreation objectCreation : module.getProgram().getObjectCreationList()) {
						assertTrue(objectCreation.getClassDeclaration() != null);
					}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
