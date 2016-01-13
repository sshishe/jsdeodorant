package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.javascript.analysis.decomposition.AbstractStatement;
import ca.concordia.javascript.analysis.decomposition.CompositeStatement;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.decomposition.Statement;
import ca.concordia.javascript.analysis.util.IdentifierHelper;
import ca.concordia.javascript.analysis.util.DebugHelper;

public class ObjectCreation extends Creation {
	static Logger log = Logger.getLogger(ObjectCreation.class.getName());
	private AbstractFunctionFragment statement;
	private NewExpressionTree newExpressionTree;
	// In one scope we can have two functions with exact same names. but the
	// last one is executed in run-time because the last one is redeclared
	private FunctionDeclaration classDeclaration;
	private AbstractExpression operandOfNew;
	private List<AbstractExpression> arguments;
	private boolean isClassDeclarationPredefined = false;
	private boolean isFunctionObject = false;
	private AbstractIdentifier identifier;
	private AbstractIdentifier aliasedIdentifier;
	private Module classDeclarationModule;

	public ObjectCreation(NewExpressionTree newExpressionTree, AbstractFunctionFragment statement) {
		this.newExpressionTree = newExpressionTree;
		this.statement = statement;
		if (IdentifierHelper.getIdentifier(newExpressionTree.operand).identifierName.equals("function"))
			setFunctionObject(true);
	}

	/**
	 * 
	 * @param newExpressionTree
	 * @param operandOfNew
	 *            the name of class that would instantiate
	 * @param arguments
	 *            passed to constructor
	 */
	public ObjectCreation(NewExpressionTree newExpressionTree, AbstractExpression operandOfNew, List<AbstractExpression> arguments) {
		this.newExpressionTree = newExpressionTree;
		this.operandOfNew = operandOfNew;
		this.arguments = arguments;
		if (this.operandOfNew.asIdentifiableExpression().getIdentifier().equals("function"))
			setFunctionObject(true);
	}

	public FunctionDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(FunctionDeclaration functionDeclaration, Module module) {
		classDeclaration = functionDeclaration;
		this.classDeclarationModule = module;
	}

	public AbstractExpression getOperandOfNew() {
		return operandOfNew;
	}

	public String getClassName() {
		AbstractIdentifier identifier = IdentifierHelper.getIdentifier(operandOfNew.getExpression());
		return Strings.isNullOrEmpty(identifier.toString()) ? "<Anonymous>" : identifier.toString();
	}

	public AbstractIdentifier getIdentifier() {
		if (identifier == null)
			identifier = IdentifierHelper.getIdentifier(operandOfNew.getExpression());
		return identifier;
	}

	public AbstractIdentifier getAliasedIdentifier() {
		if (aliasedIdentifier != null)
			return aliasedIdentifier;
		if (getIdentifier() instanceof PlainIdentifier)
			return aliasedIdentifier = identifier;
		if (statement.getParent() instanceof CompositeStatement)
			aliasedIdentifier = inspectCompositeStatement((CompositeStatement) statement.getParent());
		else if (statement.getParent() instanceof Program) {
			Program parent = ((Program) statement.getParent());
			for (SourceElement sourceElement : parent.getSourceElements()) {
				if (sourceElement instanceof Statement) {
					Statement statement = (Statement) sourceElement;
					if (!this.statement.equals(statement))
						if (statement.getStatement() instanceof VariableStatementTree || statement.getStatement() instanceof ExpressionStatementTree)
							aliasedIdentifier = detectAliasing(statement, (CompositeIdentifier) identifier);
						else
							aliasedIdentifier = detectAliasing(statement.getVariableDeclarationList(), identifier);
				} else if (sourceElement instanceof CompositeStatement)
					aliasedIdentifier = inspectCompositeStatement((CompositeStatement) sourceElement);
				if (aliasedIdentifier != null)
					return aliasedIdentifier;
			}
		}
		return identifier;
	}

	private AbstractIdentifier inspectCompositeStatement(CompositeStatement composite) {
		for (AbstractStatement statement : composite.getStatements()) {
			if (statement.getStatement() instanceof VariableStatementTree || statement.getStatement() instanceof ExpressionStatementTree) {
				AbstractIdentifier aliasedIdentifier = detectAliasing(statement, (CompositeIdentifier) identifier);
				if (aliasedIdentifier != null)
					return aliasedIdentifier;
			}
		}
		return null;
	}

	private AbstractIdentifier detectAliasing(List<VariableDeclaration> variableDeclarations, AbstractIdentifier identifier) {
		for (VariableDeclaration variableDeclaration : variableDeclarations) {
			AbstractIdentifier aliasedIdentifier = detectAliasing(variableDeclaration, identifier);
			if (aliasedIdentifier != null)
				return aliasedIdentifier;
		}
		return null;
	}

	private AbstractIdentifier detectAliasing(VariableDeclaration variableDeclaration, AbstractIdentifier identifier) {
		try {
			throw new Exception("This method should not be reached");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return identifier;
	}

	private AbstractIdentifier detectAliasing(AbstractStatement statement, CompositeIdentifier identifier) {
		if (statement.getStatement() instanceof VariableStatementTree) {
			VariableStatementTree variableStatementTree = statement.getStatement().asVariableStatement();
			for (VariableDeclarationTree variableDeclaration : variableStatementTree.declarations.declarations) {
				if (variableDeclaration.initializer == null)
					continue;
				// if new function() IIFE then continue, do not change the identifier
				if (variableDeclaration.initializer instanceof NewExpressionTree && variableDeclaration.initializer.asNewExpression().operand instanceof FunctionDeclarationTree)
					continue;
				if (IdentifierHelper.getIdentifier(variableDeclaration.lvalue).identifierName.equals(identifier.getMostLeftPart().identifierName))
					if (variableDeclaration.initializer instanceof NewExpressionTree)
						return identifier.getRightPart();
					else
						return identifier;
			}
		} else if (statement.getStatement() instanceof ExpressionStatementTree) {
			ExpressionStatementTree expression = statement.getStatement().asExpressionStatement();
			if (expression.expression instanceof BinaryOperatorTree) {
				BinaryOperatorTree binaryOperator = expression.expression.asBinaryOperator();
				if (binaryOperator.right == null)
					return null;
				if (IdentifierHelper.getIdentifier(binaryOperator.left).identifierName.equals(identifier.getMostLeftPart().identifierName))
					if (binaryOperator.right instanceof NewExpressionTree)
						return identifier.getRightPart();
					else
						return new CompositeIdentifier(IdentifierHelper.getIdentifier(binaryOperator.right), identifier.getRightPart());
			}
		}

		return null;
	}

	public List<AbstractExpression> getArguments() {
		return arguments;
	}

	public NewExpressionTree getNewExpressionTree() {
		return newExpressionTree;
	}

	public void setNewExpressionTree(NewExpressionTree newExpressionTree) {
		this.newExpressionTree = newExpressionTree;
	}

	public void setOperandOfNew(AbstractExpression operandOfNew) {
		this.operandOfNew = operandOfNew;
	}

	public void setArguments(List<AbstractExpression> arguments) {
		this.arguments = arguments;
	}

	public String toString() {
		return DebugHelper.extract(newExpressionTree);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ObjectCreation) {
			ObjectCreation toCompare = (ObjectCreation) other;
			return Objects.equals(this.newExpressionTree, toCompare.newExpressionTree);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(newExpressionTree);
	}

	public AbstractFunctionFragment getStatement() {
		return statement;
	}

	public void setStatement(AbstractFunctionFragment statement) {
		this.statement = statement;
	}

	public boolean isClassDeclarationPredefined() {
		return isClassDeclarationPredefined;
	}

	public void setClassDeclarationPredefined(boolean isClassDeclarationPredefined) {
		this.isClassDeclarationPredefined = isClassDeclarationPredefined;
	}

	public boolean isFunctionObject() {
		return isFunctionObject;
	}

	public void setFunctionObject(boolean isFunctionObject) {
		this.isFunctionObject = isFunctionObject;
	}

	public String getClassDeclarationQualifiedName() {
		if (this.isClassDeclarationPredefined)
			return getIdentifier().toString();
		else
			return this.getClassDeclaration().getQualifiedName();
	}

	public String getClassDeclarationKind() {
		if (this.getClassDeclaration() != null)
			return this.getClassDeclaration().getKind().toString();
		else
			return "";
	}

	public String getObjectCreationLocation() {
		return this.getNewExpressionTree().location.toString();
	}

	public String getClassDeclarationLocation() {
		if (this.getClassDeclaration() != null)
			return this.getClassDeclaration().getFunctionDeclarationTree().location.toString();
		else
			return "";
	}

	public Module getClassDeclarationModule() {
		return classDeclarationModule;
	}

	public void setClassDeclarationModule(Module classDeclarationModule) {
		this.classDeclarationModule = classDeclarationModule;
	}
}
