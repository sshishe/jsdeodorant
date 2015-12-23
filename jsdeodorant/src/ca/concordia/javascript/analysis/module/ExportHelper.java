package ca.concordia.javascript.analysis.module;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.Export;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class ExportHelper {
	static Logger log = Logger.getLogger(ExportHelper.class.getName());
	private Module currentModule;
	private List<Module> modules;

	public ExportHelper(Module module, List<Module> modules) {
		this.currentModule = module;
		this.modules = modules;
	}

	public void extract(ParseTree expression) {
		if (expression instanceof ExpressionStatementTree) {
			ExpressionStatementTree expressionStatement = expression.asExpressionStatement();
			if (expressionStatement.expression instanceof BinaryOperatorTree)
				if (checkIfLValueIsRequireStatement(expressionStatement.expression.asBinaryOperator())) {
					//matchModules();
					return;
				}
		}

	}

	private boolean checkIfLValueIsRequireStatement(BinaryOperatorTree binaryOperator) {
		AbstractIdentifier lValueIdentifier = IdentifierHelper.getIdentifier(binaryOperator.left);
		if (lValueIdentifier instanceof PlainIdentifier)
			return false;
		if (lValueIdentifier.toString().contains("module.exports") || lValueIdentifier.asCompositeIdentifier().getLeftPart().toString().contains("exports")) {
			PlainIdentifier exportIdentifier = lValueIdentifier.asCompositeIdentifier().getMostRightPart();
			Export export = new Export(exportIdentifier.getIdentifierName(), new AbstractExpression(binaryOperator.right));
			currentModule.addExport(export);
		}
		return false;
	}
}
