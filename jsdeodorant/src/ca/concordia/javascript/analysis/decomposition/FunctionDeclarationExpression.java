package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.QualifiedName;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.util.QualifiedNameExtractor;

import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

public class FunctionDeclarationExpression extends AbstractExpression implements
		SourceContainer, FunctionDeclaration {
	private static final Logger log = Logger
			.getLogger(FunctionDeclarationExpression.class.getName());
	private String name;
	private FunctionKind kind;
	private List<AbstractExpression> parameters;
	private List<AbstractStatement> statementList;

	public FunctionDeclarationExpression(
			FunctionDeclarationTree functionDeclarationTree,
			SourceContainer parent) {
		super(functionDeclarationTree, parent);
		this.statementList = new ArrayList<>();
		this.parameters = new ArrayList<>();
		if (functionDeclarationTree.name != null)
			this.name = functionDeclarationTree.name.value;

		this.kind = FunctionKind.valueOf(functionDeclarationTree.kind
				.toString());

		if (functionDeclarationTree.formalParameterList != null) {
			FormalParameterListTree formalParametersList = functionDeclarationTree.formalParameterList
					.asFormalParameterList();
			for (ParseTree parameter : formalParametersList.parameters)
				this.addParameter(new AbstractExpression(parameter));
		}
		StatementProcessor.processStatement(
				functionDeclarationTree.functionBody, this);
	}

	@Override
	public void addElement(SourceElement element) {
		if (element instanceof AbstractStatement)
			addStatement((AbstractStatement) element);
	}

	public void addStatement(AbstractStatement statement) {
		statementList.add(statement);
	}

	public List<AbstractStatement> getStatements() {
		return statementList;
	}

	public QualifiedName getQualifiedNameObject() {
		QualifiedName qn = new QualifiedName();
		if (getParent() instanceof ObjectLiteralExpression) {
			ObjectLiteralExpression objectLiteralExpression = (ObjectLiteralExpression) getParent();
			for (String key : objectLiteralExpression.getPropertyMap().keySet())
				if (objectLiteralExpression.getPropertyMap().get(key)
						.equals(this))
					return new QualifiedName(key);
		}
		if (getParent() instanceof CompositeStatement) {
			CompositeStatement parent = (CompositeStatement) getParent();
			for (AbstractStatement statement : parent.getStatements()) {
				for (FunctionDeclarationExpression functionDeclarationExpression : statement
						.getFuntionDeclarationExpressions())
					if (functionDeclarationExpression.equals(this)) {
						if (statement.getStatement() instanceof ExpressionStatementTree) {
							ExpressionStatementTree epxressionStatement = statement
									.getStatement().asExpressionStatement();
							if (epxressionStatement.expression instanceof BinaryOperatorTree) {
								return QualifiedNameExtractor.getQualifiedName(
										epxressionStatement.expression
												.asBinaryOperator().left, qn);
							}
						}
						if (statement.getStatement() instanceof VariableStatementTree) {
							VariableStatementTree variableStatement = statement
									.getStatement().asVariableStatement();
							VariableDeclarationListTree variableDeclarationListTree = variableStatement.declarations;
							for (VariableDeclarationTree variableDeclaration : variableDeclarationListTree.declarations)
								if (variableDeclaration.initializer != null
										&& variableDeclaration.initializer
												.equals(functionDeclarationExpression
														.getExpression()))
									return QualifiedNameExtractor
											.getQualifiedName(
													variableDeclaration.lvalue,
													qn);
						}
					}
			}
		}
		return null;
	}

	public String getName() {
		return getQualifiedNameObject().toString();
	}

	public String getNormalizedName() {
		return QualifiedNameExtractor
				.getNormalizedName(getQualifiedNameObject());
	}

	public FunctionKind getKind() {
		return kind;
	}

	public List<AbstractExpression> getParameters() {
		return parameters;
	}

	private void addParameter(AbstractExpression parameter) {
		this.parameters.add(parameter);
	}

	public FunctionDeclarationTree getFunctionDeclarationTree() {
		return (FunctionDeclarationTree) getExpression();
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		for (AbstractStatement statement : statementList) {
			functionDeclarations.addAll(statement.getFunctionDeclarations());
		}
		return functionDeclarations;
	}
}
