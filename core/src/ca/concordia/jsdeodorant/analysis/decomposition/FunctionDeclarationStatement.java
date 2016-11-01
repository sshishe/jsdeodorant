package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.abstraction.StatementProcessor;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;

public class FunctionDeclarationStatement extends CompositeStatement implements FunctionDeclaration, Identifiable {
	private AbstractIdentifier identifier;
	private FunctionKind kind;
	private List<AbstractExpression> parameters;
	private FunctionDeclarationTree functionDeclarationTree;
	private boolean isClassDeclaration = false;
	// for codes transpiled from typeScript and coffeeScript we have class and constructor
	private boolean isConstructor = false;
	private boolean hasReturn;

	public FunctionDeclarationStatement(FunctionDeclarationTree functionDeclarationTree, SourceContainer parent) {
		super(functionDeclarationTree, StatementType.FUNCTION_DECLARATION, parent);
		this.functionDeclarationTree = functionDeclarationTree;
		this.parameters = new ArrayList<>();
		this.identifier = getIdentifier();
		this.kind = FunctionKind.valueOf(functionDeclarationTree.kind.toString());
		this.hasReturn=false;
		if (functionDeclarationTree.formalParameterList != null) {
			FormalParameterListTree formalParametersList = functionDeclarationTree.formalParameterList.asFormalParameterList();
			for (ParseTree parameter : formalParametersList.parameters)
				this.addParameter(new AbstractExpression(parameter));
		}
		StatementProcessor.processStatement(functionDeclarationTree.functionBody, this);
	}

	public String getName() {
		return Strings.isNullOrEmpty(identifier.toString()) ? "<Anonymous>" : identifier.toString();
	}

	public String getQualifiedName() {
		return getName();
	}

	public AbstractIdentifier getIdentifier() {
		if (identifier == null)
			identifier = buildIdentifier();
		return identifier;
	}

	public AbstractIdentifier buildIdentifier() {
		return IdentifierHelper.getIdentifier(functionDeclarationTree);
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
		return (FunctionDeclarationTree) getStatement();
	}

	public boolean isTypeDeclaration() {
		return isClassDeclaration;
	}

	public void setClassDeclaration(boolean state) {
		this.isClassDeclaration = state;
	}

	public List<AbstractStatement> getReturnStatementList() {
		return getReturnStatementListExtracted(getStatements());
	}

	@Override
	public List<AbstractExpression> getAssignments() {
		return getAssignmentExpressionList();
	}

	@Override
	public AbstractIdentifier getRawIdentifier() {
		return IdentifierHelper.getIdentifier(functionDeclarationTree);
	}
	
	@Override
	public boolean isConstructor() {
		return isConstructor;
	}
	
	public void SetIsConstructor(boolean isConstructor) {
		this.isConstructor=isConstructor;
	}

	@Override
	public boolean hasReturnStatement() {
		return this.hasReturn;
	}

	@Override
	public void setHasReturnStatement(boolean flag) {
		this.hasReturn=flag;
		
	}

}
