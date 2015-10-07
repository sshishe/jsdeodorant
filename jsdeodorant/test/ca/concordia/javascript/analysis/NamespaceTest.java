package ca.concordia.javascript.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclarationExpression;
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

	private AnalysisResult setAnalysisForObjectLiteral() throws IOException {
		testRunner.setJsFile("test/namespace/object-literal.js");
		AnalysisResult result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testObjectLiteralNamespaceNumberOfClassDeclaration() {
		try {
			AnalysisResult result = setAnalysisForObjectLiteral();
			assertTrue(result.getProgram().getClassDeclarationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testObjectLiteralNamespaceWithQualifiedName() {
		try {
			//TODO update based on recent changes
			AnalysisResult result = setAnalysisForObjectLiteral();
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("yourNamespace.Foo") || qualifiedName.equals("yourNamespace.Bar"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AnalysisResult setAnalysisForIIFE(String fileName) throws IOException {
		testRunner.setJsFile(fileName);
		AnalysisResult result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testIIFEAssignToVarNamespaceNumberOfClassDeclaration() {
		try {
			AnalysisResult result = setAnalysisForIIFE("test/namespace/iife-assign-to-var.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFEAssignToVarNamespaceWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForIIFE("test/namespace/iife-assign-to-var.js");
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("someObj.publicClass") || qualifiedName.equals("someObj.innerObj.deepInnerClass"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFEPassAsParameterWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForIIFE("test/namespace/iife-with-parameter.js");
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("namespace.sayHello"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFEWithApplyMethodWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForIIFE("test/namespace/iife-with-apply.js");
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("myApp.utils.getValue") || qualifiedName.equals("myApp.utils.tools.diagnose"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFEObjLiteralNamespaceNumberOfClassDeclaration() {
		try {
			AnalysisResult result = setAnalysisForIIFE("test/namespace/iife-obj-literal.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AnalysisResult setAnalysisForNewFunction() throws IOException {
		testRunner.setJsFile("test/namespace/new-function.js");
		AnalysisResult result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testNewFunctionNamespaceWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForNewFunction();
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				assertTrue(functionDeclaration.getQualifiedName().equals("ns.publicFunction"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNewFunctionNamespaceNumberOfClasses() {
		try {
			AnalysisResult result = setAnalysisForNewFunction();
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AnalysisResult setAnalysisForNestedObjectLiterals(String fileName) throws IOException {
		testRunner.setJsFile(fileName);
		AnalysisResult result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testNestedObjectLiterals() {
		try {
			AnalysisResult result = setAnalysisForNestedObjectLiterals("test/namespace/nested-object-literals.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
			assertEquals(result.getProgram().getClassDeclarationList().get(0).getQualifiedName(), "application.utilities.drawing.canvas.test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExternalAliasing() {
		try {
			AnalysisResult result = setAnalysisForNestedObjectLiterals("test/namespace/external-aliased.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
			assertEquals(result.getProgram().getClassDeclarationList().get(0).getQualifiedName(), "namespace.innerNamespace.Foo");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
