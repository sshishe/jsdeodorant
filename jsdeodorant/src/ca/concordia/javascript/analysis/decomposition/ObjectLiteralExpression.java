package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.IdentifierToken;
import com.google.javascript.jscomp.parsing.parser.LiteralToken;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;

public class ObjectLiteralExpression extends AbstractExpression implements SourceContainer {

	private Map<String, AbstractExpression> propertyMap;

	public ObjectLiteralExpression(ObjectLiteralExpressionTree objectLiteral, SourceContainer parent) {
		super(objectLiteral, parent);
		ImmutableList<ParseTree> nameAndValues = objectLiteral.propertyNameAndValues;
		this.propertyMap = new LinkedHashMap<>();
		for (ParseTree argument : nameAndValues) {
			if (argument instanceof PropertyNameAssignmentTree) {
				// TODO handle nested properties
				PropertyNameAssignmentTree propertyNameAssignment = (PropertyNameAssignmentTree) argument;
				Token token = propertyNameAssignment.name;
				String name = null;
				if (token instanceof IdentifierToken) {
					name = token.asIdentifier().value;
				} else if (token instanceof LiteralToken) {
					name = token.asLiteral().value;
				}
				ParseTree value = propertyNameAssignment.value;
				AbstractExpression valueExpression = null;
				if(value instanceof FunctionDeclarationTree) {
					FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree)value;
					valueExpression = new FunctionDeclarationExpression(functionDeclarationTree, this);
				}
				else {
					valueExpression = new AbstractExpression(value);
				}
				propertyMap.put(name, valueExpression);
			}
		}
	}

	@Override
	public void addElement(SourceElement element) {
		// TODO Auto-generated method stub
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
		List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
		List<FunctionDeclarationExpression> functionDeclarationExpressions = new ArrayList<>();
		for (String key : propertyMap.keySet()) {
			AbstractExpression abstractExpression = propertyMap.get(key);
			if (abstractExpression instanceof FunctionDeclarationExpression) {
				functionDeclarationExpressions.add((FunctionDeclarationExpression)abstractExpression);
			}
		}
		for (FunctionDeclarationExpression functionDeclarationExpression : functionDeclarationExpressions) {
			functionDeclarations.add(functionDeclarationExpression);
			functionDeclarations.addAll(functionDeclarationExpression.getFunctionDeclarations());
		}
		return functionDeclarations;
	}
}
