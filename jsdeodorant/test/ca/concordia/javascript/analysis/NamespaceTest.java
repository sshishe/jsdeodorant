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
		AnalysisResult result = testRunner.performActions();
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

	private AnalysisResult setAnalysisForIIFE() throws IOException {
		testRunner.setJsFile("test/namespace/iife.js");
		AnalysisResult result = testRunner.performActions();
		return result;
	}

	@Test
	public void testIIFENamespaceNumberOfClassDeclaration() {
		try {
			AnalysisResult result = setAnalysisForIIFE();
			assertTrue(result.getProgram().getClassDeclarationList().size() == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIIFENamespaceWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForIIFE();
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("someObj.publicClass") || qualifiedName.equals("someObj.innerObj.deepInnerClass"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AnalysisResult setAnalysisForNewFunction() throws IOException {
		testRunner.setJsFile("test/namespace/new-function.js");
		AnalysisResult result = testRunner.performActions();
		return result;
	}

	@Test
	public void testNewFunctionNamespaceWithQualifiedName() {
		try {
			AnalysisResult result = setAnalysisForNewFunction();
			for (FunctionDeclaration functionDeclaration : result.getProgram().getClassDeclarationList()) {
				String qualifiedName = ((FunctionDeclarationExpression) functionDeclaration).getQualifiedName();
				assertTrue(qualifiedName.equals("ns.publicFunction"));
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
}
