package ca.concordia.javascript.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ca.concordia.javascript.analysis.abstraction.JSPackage;
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

	private JSPackage setAnalysisForObjectLiteral() throws IOException {
		testRunner.setJsFile("test/namespace/object-literal.js");
		JSPackage result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testObjectLiteralNamespaceNumberOfClassDeclaration() {
		try {
			JSPackage result = setAnalysisForObjectLiteral();
			assertTrue(result.getProgram().getClassDeclarationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testObjectLiteralNamespaceWithQualifiedName() {
		try {
			//TODO update based on recent changes
			JSPackage result = setAnalysisForObjectLiteral();
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("yourNamespace.Foo") || qualifiedName.equals("yourNamespace.Bar"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSPackage setAnalysisForIIFE(String fileName) throws IOException {
		testRunner.setJsFile(fileName);
		JSPackage result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testIIFEAssignToVarNamespaceNumberOfClassDeclaration() {
		try {
			JSPackage result = setAnalysisForIIFE("test/namespace/iife-assign-to-var.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFEAssignToVarNamespaceWithQualifiedName() {
		try {
			JSPackage result = setAnalysisForIIFE("test/namespace/iife-assign-to-var.js");
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
			JSPackage result = setAnalysisForIIFE("test/namespace/iife-with-parameter.js");
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
			JSPackage result = setAnalysisForIIFE("test/namespace/iife-with-apply.js");
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
			JSPackage result = setAnalysisForIIFE("test/namespace/iife-obj-literal.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSPackage setAnalysisForNewFunction() throws IOException {
		testRunner.setJsFile("test/namespace/new-function.js");
		JSPackage result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testNewFunctionNamespaceWithQualifiedName() {
		try {
			JSPackage result = setAnalysisForNewFunction();
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
			JSPackage result = setAnalysisForNewFunction();
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSPackage setAnalysisForNestedObjectLiterals(String fileName) throws IOException {
		testRunner.setJsFile(fileName);
		JSPackage result = testRunner.performActionsForTest();
		return result;
	}

	@Test
	public void testNestedObjectLiterals() {
		try {
			JSPackage result = setAnalysisForNestedObjectLiterals("test/namespace/nested-object-literals.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
			assertEquals(result.getProgram().getClassDeclarationList().get(0).getQualifiedName(), "application.utilities.drawing.canvas.test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExternalAliasing() {
		try {
			JSPackage result = setAnalysisForNestedObjectLiterals("test/namespace/external-aliased.js");
			assertTrue(result.getProgram().getClassDeclarationList().size() == 1);
			assertEquals(result.getProgram().getClassDeclarationList().get(0).getQualifiedName(), "namespace.innerNamespace.Foo");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
