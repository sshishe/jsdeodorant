package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class FunctionDeclarationStatement extends CompositeStatement implements FunctionDeclaration, Identifiable {
	private AbstractIdentifier identifier;
	private FunctionKind kind;
	private List<AbstractExpression> parameters;
	private FunctionDeclarationTree functionDeclarationTree;
	private boolean isClassDeclaration = false;

	public FunctionDeclarationStatement(FunctionDeclarationTree functionDeclarationTree, SourceContainer parent) {
		super(functionDeclarationTree, StatementType.FUNCTION_DECLARATION, parent);
		this.functionDeclarationTree = functionDeclarationTree;
		this.parameters = new ArrayList<>();
		this.identifier = getIdentifier();
		this.kind = FunctionKind.valueOf(functionDeclarationTree.kind.toString());

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

	public boolean isClassDeclaration() {
		return isClassDeclaration;
	}

	public void setClassDeclaration(boolean state) {
		this.isClassDeclaration = state;
	}

	public List<AbstractStatement> getReturnStatementList() {
		return getReturnStatementListExtracted(getStatements());
	}

}
