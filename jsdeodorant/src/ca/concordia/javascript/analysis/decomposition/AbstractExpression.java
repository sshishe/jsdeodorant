package ca.concordia.javascript.analysis.decomposition;

import java.util.Objects;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.Namespace;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.util.ExpressionExtractor;
import ca.concordia.javascript.analysis.util.DebugHelper;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class AbstractExpression extends AbstractFunctionFragment {
	private static final Logger log = Logger.getLogger(AbstractExpression.class.getName());
	private ParseTree expression;
	private Namespace namespace;

	public AbstractExpression(ParseTree expression) {
		super(null);
		this.expression = expression;
	}

	public AbstractExpression(ParseTree expression, SourceContainer parent) {
		super(parent);
		this.expression = expression;
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		processFunctionInvocations(expressionExtractor.getCallExpressions(expression));
		processVariableDeclarations(expressionExtractor.getVariableDeclarationExpressions(expression));
		processNewExpressions(expressionExtractor.getNewExpressions(expression));
		processArrayLiteralExpressions(expressionExtractor.getArrayLiteralExpressions(expression));
	}

	public ParseTree getExpression() {
		return expression;
	}

	public String toString() {
		return DebugHelper.extract(expression);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractExpression) {
			AbstractExpression toCompare = (AbstractExpression) other;
			return Objects.equals(this.expression, toCompare.expression);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}

	public Namespace getNamespace() {
		if (namespace == null) {
			SourceContainer parent = getParent();
			buildNamespaceStructure(parent);
		}
		return namespace;
	}

	public boolean hasNamespace() {
		return getNamespace() != null;
	}

	private Namespace buildNamespaceStructure(SourceContainer parent) {
		return buildNamespaceStructure(parent, null);
	}

	private Namespace buildNamespaceStructure(SourceContainer parent, Namespace namespace) {
		if (parent instanceof Program)
			return namespace;
		if (parent instanceof CompositeStatement)
			buildNamespaceStructure(((CompositeStatement) parent).getParent(), namespace);
		else {
			AbstractExpression parentExpression = (AbstractExpression) parent;
			if (namespace == null)
				this.namespace = namespace = new Namespace(parentExpression);
			else
				namespace = namespace.setParent(new Namespace(parentExpression));
			if (parentExpression.getParent() != null)
				buildNamespaceStructure(parentExpression.getParent(), namespace);
		}
		return this.namespace;
	}
}
