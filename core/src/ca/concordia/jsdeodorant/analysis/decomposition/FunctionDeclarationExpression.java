package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceElement;
import ca.concordia.jsdeodorant.analysis.abstraction.StatementProcessor;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class FunctionDeclarationExpression extends AbstractExpression implements SourceContainer, FunctionDeclaration, IdentifiableExpression {
	private static final Logger log = Logger.getLogger(FunctionDeclarationExpression.class.getName());
	private AbstractIdentifier identifier;
	private AbstractIdentifier publicIdentifier;
	private FunctionKind kind;
	private ParseTree leftValueExpression;
	// in object literal expressions the left value token would be the key of
	// the {key/value}
	private Token leftValueToken;
	private FunctionDeclarationExpressionNature functionDeclarationExpressionNature;
	private boolean isClassDeclaration = false;

	private List<AbstractExpression> parameters;
	private List<AbstractStatement> statementList;

	public FunctionDeclarationExpression(FunctionDeclarationTree functionDeclarationTree, FunctionDeclarationExpressionNature functionDeclarationExpressionNature, SourceContainer parent) {
		super(functionDeclarationTree, parent);
		this.statementList = new ArrayList<>();
		this.functionDeclarationExpressionNature = functionDeclarationExpressionNature;
		this.parameters = new ArrayList<>();
		this.kind = FunctionKind.valueOf(functionDeclarationTree.kind.toString());

		if (functionDeclarationTree.formalParameterList != null) {
			FormalParameterListTree formalParametersList = functionDeclarationTree.formalParameterList.asFormalParameterList();
			for (ParseTree parameter : formalParametersList.parameters)
				this.addParameter(new AbstractExpression(parameter));
		}
		StatementProcessor.processStatement(functionDeclarationTree.functionBody, this);
	}

	public FunctionDeclarationExpression(FunctionDeclarationTree functionDeclarationTree, FunctionDeclarationExpressionNature functionDeclarationExpressionNature, ParseTree leftValueExpression, SourceContainer parent) {
		this(functionDeclarationTree, functionDeclarationExpressionNature, parent);
		this.leftValueExpression = leftValueExpression;
	}

	@Override
	public void addElement(SourceElement element) {
		if (element instanceof AbstractStatement)
			addStatement((AbstractStatement) element);
	}

	public void addStatement(AbstractStatement statement) {
		statementList.add(statement);
	}

	public AbstractIdentifier getIdentifier() {
		if (identifier == null)
			identifier = buildInternalIdentifier();
		return identifier;
	}

	/**
	 * if this method return null we might have IIFE without any lValue
	 */
	public String getName() {
		identifier = getIdentifier();
		return getName(identifier);
	}

	public String getName(AbstractIdentifier identifier) {
		if (identifier == null)
			return "<Anonymous>";
		if (Strings.isNullOrEmpty(identifier.toString()))
			return "<Anonymous>";
		return identifier.toString();
	}

	public String getQualifiedName() {
		this.publicIdentifier = getPublicIdentifier();
		if (hasNamespace())
			if (getNamespace().getPart().isIdentifiableExpression())
				if (getNamespace().getPart().asIdentifiableExpression().getName().equals("<Anonymous>"))
					return getName(publicIdentifier);
				else
					return getNamespace() + "." + getName(publicIdentifier);
		if (hasModuleInformation())
			refineModuleInformation();
		return getName(publicIdentifier);
	}

	private void refineModuleInformation() {
		this.asIdentifiableExpression().setPublicIdentifier(publicIdentifier.asCompositeIdentifier().getMostRightPart());
	}

	private boolean hasModuleInformation() {
		if (publicIdentifier instanceof CompositeIdentifier)
			if (publicIdentifier.asCompositeIdentifier().getMostLeftPart().equals("exports") || publicIdentifier.asCompositeIdentifier().getLeftPart().toString().contains("module.exports"))
				return true;
		return false;
	}

	public AbstractIdentifier getPublicIdentifier() {
		if (publicIdentifier == null)
			publicIdentifier = getIdentifier();
		return publicIdentifier;
	}

	public void setPublicIdentifier(AbstractIdentifier publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	/**
	 * This method tries to retrieve the name of the Function Declaration
	 * Expression. leftValueToken will be assigned when we have
	 * ObjectLiteralExpression which the operand would be the key of key/value
	 * leftValueExpression also would be assigned when we have
	 * VariableStatement, NewExpression, Call Expression and
	 * BinaryOperationExpression If leftValue{token|expression} is null, the
	 * method tries to find the parent function and find the lValue from there,
	 * it would be ParenExpression or something like that which at the creation
	 * time it's not possible to access the lValue
	 */
	private AbstractIdentifier buildInternalIdentifier() {
		if (leftValueExpression != null) {
			AbstractIdentifier normalizedIdentifier = normalizePrototype(IdentifierHelper.getIdentifier(leftValueExpression));
			return inspectParentNamespacing(normalizedIdentifier);
		} else if (getParent() instanceof ObjectLiteralExpression) {
			ObjectLiteralExpression objectLiteralExpression = (ObjectLiteralExpression) getParent();
			for (Token key : objectLiteralExpression.getPropertyMap().keySet())
				if (objectLiteralExpression.getPropertyMap().get(key).equals(this))
					return new PlainIdentifier(key);
		} else if (getParent() instanceof CompositeStatement) {
			CompositeStatement parent = (CompositeStatement) getParent();
			for (AbstractStatement statement : parent.getStatements()) {
				for (FunctionDeclarationExpression functionDeclarationExpression : statement.getFunctionDeclarationExpressionList())
					if (functionDeclarationExpression.equals(this)) {
						return IdentifierHelper.findLValue(statement, functionDeclarationExpression.getExpression());
					}
			}
		} else if (getParent() instanceof Program) {
			Program parent = ((Program) getParent());
			for (FunctionDeclaration functionDeclaration : parent.getFunctionDeclarationList())
				if (functionDeclaration.equals(this))
					//					//return IdentifierHelper.findLValue((AbstractStatement) functionDeclaration, functionDeclaration.getFunctionDeclarationTree());
					return new PlainIdentifier(functionDeclaration.getFunctionDeclarationTree());
		} else if (leftValueToken != null)
			return new PlainIdentifier(leftValueToken);
		return null;
	}

	private AbstractIdentifier inspectParentNamespacing(AbstractIdentifier normalizedIdentifier) {
		SourceContainer parent = this.getParent();
		if (parent instanceof Program) {

		}

		if (parent instanceof CompositeStatement) {
			CompositeStatement composite = (CompositeStatement) parent;
			for (AbstractStatement statement : composite.getStatements()) {
				if (statement.getStatement() instanceof ExpressionStatementTree) {
					ExpressionStatementTree expressionStatementTree = statement.getStatement().asExpressionStatement();
					if (expressionStatementTree.expression instanceof BinaryOperatorTree) {
						BinaryOperatorTree binaryOperator = expressionStatementTree.expression.asBinaryOperator();
						AbstractIdentifier rightPart = IdentifierHelper.getIdentifier(binaryOperator.right);

						if (normalizedIdentifier.toString().equals(rightPart.toString()))
							return IdentifierHelper.getIdentifier(binaryOperator.left);
					}
				}
			}
		}
		return normalizedIdentifier;

	}

	private AbstractIdentifier normalizePrototype(AbstractIdentifier identifierToBeNormalized) {
		return IdentifierHelper.removePart(identifierToBeNormalized, "prototype");
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

	public List<FunctionDeclaration> getFunctionDeclarationList() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		for (AbstractStatement statement : statementList) {
			functionDeclarations.addAll(statement.getFunctionDeclarationList());
		}
		return functionDeclarations;
	}

	public ParseTree getLeftValueExpression() {
		return leftValueExpression;
	}

	public void setLeftValueExpression(ParseTree leftValueOfExpression) {
		this.leftValueExpression = leftValueOfExpression;
	}

	public Token getLeftValueToken() {
		return leftValueToken;
	}

	public void setLeftValueToken(Token leftValueToken) {
		this.leftValueToken = leftValueToken;
	}

	public FunctionDeclarationExpressionNature getFunctionDeclarationExpressionNature() {
		return functionDeclarationExpressionNature;
	}

	public void setFunctionDeclarationExpressionNature(FunctionDeclarationExpressionNature functionDeclarationExpressionNature) {
		this.functionDeclarationExpressionNature = functionDeclarationExpressionNature;
	}

	public List<AbstractStatement> getReturnStatementList() {
		return getReturnStatementListExtracted(getStatements());
	}

	public boolean isClassDeclaration() {
		return isClassDeclaration;
	}

	public void setClassDeclaration(boolean state) {
		this.isClassDeclaration = state;
	}

	@Override
	public List<AbstractStatement> getStatements() {
		return statementList;
	}

	@Override
	public List<AbstractExpression> getAssignments() {
		return getAssignmentExpressionList();
	}

	@Override
	public AbstractIdentifier getRawIdentifier() {
		if (leftValueExpression == null){
			return null;
		}
		return IdentifierHelper.getIdentifier(leftValueExpression);
	}
}
