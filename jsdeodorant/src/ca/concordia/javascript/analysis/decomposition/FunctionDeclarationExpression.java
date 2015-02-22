package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.abstraction.StatementProcessor;

import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

public class FunctionDeclarationExpression extends AbstractExpression implements SourceContainer, FunctionDeclaration {

	private String name;
	private FunctionKind kind;
	private List<AbstractExpression> parameters;
	private List<AbstractStatement> statementList;
	
	public FunctionDeclarationExpression(FunctionDeclarationTree functionDeclarationTree,
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
		StatementProcessor.processStatement(functionDeclarationTree.functionBody, this);
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

	public String getName() {
		return name;
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
		return (FunctionDeclarationTree)getExpression();
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		for (AbstractStatement statement : statementList) {
			functionDeclarations.addAll(statement.getFunctionDeclarations());
		}
		return functionDeclarations;
	}
}
