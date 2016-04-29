package ca.concordia.jsdeodorant.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;

import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;
import ca.concordia.jsdeodorant.analysis.decomposition.AbstractFunctionFragment;
import ca.concordia.jsdeodorant.analysis.decomposition.ClassDeclaration;
import ca.concordia.jsdeodorant.analysis.util.DebugHelper;
import ca.concordia.jsdeodorant.analysis.util.ExternalAliasHelper;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;
import ca.concordia.jsdeodorant.analysis.util.SourceLocationHelper;

public class ObjectCreation extends Creation {
	static Logger log = Logger.getLogger(ObjectCreation.class.getName());
	private AbstractFunctionFragment statement;
	private NewExpressionTree newExpressionTree;
	// In one scope we can have two functions with exact same names. but the
	// last one is executed in run-time because the last one is redeclared
	private ClassDeclaration classDeclaration;
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

	public ClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(ClassDeclaration classDeclaration, Module module) {
		this.classDeclaration = classDeclaration;
		this.classDeclarationModule = module;
	}

	public AbstractExpression getOperandOfNew() {
		return operandOfNew;
	}

	public String getOperandOfNewName() {
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
		return aliasedIdentifier = ExternalAliasHelper.getAliasedIdentifier(statement, getIdentifier());
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
			return this.getClassDeclaration().getFunctionDeclaration().getQualifiedName();
	}

	public String getClassDeclarationKind() {
		if (this.getClassDeclaration() != null)
			return this.getClassDeclaration().getFunctionDeclaration().getKind().toString();
		else
			return "";
	}

	public String getObjectCreationLocation() {
		return SourceLocationHelper.getLocation(this.newExpressionTree.location);
	}

	public String getClassDeclarationLocation() {
		if (this.getClassDeclaration() != null) {
			return SourceLocationHelper.getLocation(this.getClassDeclaration().getFunctionDeclaration().getFunctionDeclarationTree().location);
		}

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
