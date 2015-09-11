package ca.concordia.javascript.analysis.decomposition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.concordia.javascript.analysis.abstraction.AbstractIdentifier;
import ca.concordia.javascript.analysis.abstraction.PlainIdentifier;
import ca.concordia.javascript.analysis.abstraction.Program;
import ca.concordia.javascript.analysis.abstraction.SourceContainer;
import ca.concordia.javascript.analysis.abstraction.SourceElement;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;

public class ObjectLiteralExpression extends AbstractExpression implements SourceContainer, IdentifiableExpression {
	static Logger log = Logger.getLogger(ObjectLiteralExpression.class.getName());
	private AbstractIdentifier internalIdentifier;
	private AbstractIdentifier publicIdentifier;
	private Map<Token, AbstractExpression> propertyMap;
	private ObjectLiteralExpressionTree objectLiteralTree;

	public ObjectLiteralExpression(ObjectLiteralExpressionTree objectLiteralTree, SourceContainer parent) {
		super(objectLiteralTree, parent);
		this.objectLiteralTree = objectLiteralTree;
		ImmutableList<ParseTree> nameAndValues = objectLiteralTree.propertyNameAndValues;
		this.propertyMap = new LinkedHashMap<>();
		for (ParseTree argument : nameAndValues) {
			if (argument instanceof PropertyNameAssignmentTree) {
				// TODO handle nested properties
				PropertyNameAssignmentTree propertyNameAssignment = (PropertyNameAssignmentTree) argument;
				Token token = propertyNameAssignment.name;
				ParseTree value = propertyNameAssignment.value;
				AbstractExpression valueExpression = null;
				if (value instanceof FunctionDeclarationTree) {
					FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree) value;
					valueExpression = new FunctionDeclarationExpression(functionDeclarationTree, FunctionDeclarationExpressionNature.OBJECT_LITERAL, this);
					((FunctionDeclarationExpression) valueExpression).setLeftValueToken(token);
				} else if (value instanceof ObjectLiteralExpressionTree) {
					ObjectLiteralExpressionTree objectLiteralExpressionTree = (ObjectLiteralExpressionTree) value;
					valueExpression = new ObjectLiteralExpression(objectLiteralExpressionTree, this);
				} else {
					valueExpression = new AbstractExpression(value);
				}
				propertyMap.put(token, valueExpression);
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
		for (Token key : propertyMap.keySet()) {
			AbstractExpression abstractExpression = propertyMap.get(key);
			if (abstractExpression instanceof FunctionDeclarationExpression) {
				functionDeclarationExpressions.add((FunctionDeclarationExpression) abstractExpression);
			}
			if (abstractExpression instanceof ObjectLiteralExpression) {
				ObjectLiteralExpression objectLiteralExpression = (ObjectLiteralExpression) abstractExpression;
				for (FunctionDeclaration functionDeclaration : objectLiteralExpression.getFunctionDeclarations()) {
					functionDeclarationExpressions.add((FunctionDeclarationExpression) functionDeclaration);
				}
			}
		}
		for (FunctionDeclarationExpression functionDeclarationExpression : functionDeclarationExpressions) {
			functionDeclarations.add(functionDeclarationExpression);
			functionDeclarations.addAll(functionDeclarationExpression.getFunctionDeclarationList());
		}
		return functionDeclarations;
	}

	public List<ObjectLiteralExpression> getObjectLiterals() {
		List<ObjectLiteralExpression> objectLiterals = new ArrayList<>();
		List<ObjectLiteralExpression> objectLiteralExpressions = new ArrayList<>();
		for (Token key : propertyMap.keySet()) {
			AbstractExpression abstractExpression = propertyMap.get(key);
			if (abstractExpression instanceof ObjectLiteralExpression) {
				objectLiteralExpressions.add((ObjectLiteralExpression) abstractExpression);
			}
		}
		for (ObjectLiteralExpression objectLiteralExpression : objectLiteralExpressions) {
			objectLiterals.add(objectLiteralExpression);
			objectLiterals.addAll(objectLiteralExpression.getObjectLiterals());
		}
		return objectLiterals;
	}

	public Map<Token, AbstractExpression> getPropertyMap() {
		return propertyMap;
	}

	public AbstractIdentifier getIdentifier() {
		if (internalIdentifier == null)
			internalIdentifier = buildInternalIdentifier();
		return internalIdentifier;
	}

	private AbstractIdentifier buildInternalIdentifier() {
		if (getParent() instanceof Program) {
			Program program = (Program) getParent();
			for (SourceElement sourceElement : program.getSourceElements()) {
				if (sourceElement instanceof AbstractStatement) {
					AbstractStatement statement = (AbstractStatement) sourceElement;
					AbstractIdentifier identifier = getIdentifierFromObjectLiteralList(statement);
					if (identifier != null)
						return identifier;
				}
			}

		} else if (getParent() instanceof CompositeStatement) {
			CompositeStatement composite = (CompositeStatement) getParent();
			for (AbstractStatement statement : composite.getStatements()) {
				AbstractIdentifier identifier = getIdentifierFromObjectLiteralList(statement);
				if (identifier != null)
					return identifier;
			}
		} else if (getParent() instanceof AbstractExpression) {
			AbstractExpression parentExpression = (AbstractExpression) getParent();
			if (parentExpression instanceof ObjectLiteralExpression) {
				ObjectLiteralExpression parentObjectLiteral = (ObjectLiteralExpression) parentExpression;
				for (Token key : parentObjectLiteral.propertyMap.keySet()) {
					AbstractExpression abstractExpression = parentObjectLiteral.propertyMap.get(key);
					if (abstractExpression.equals(this)) {
						return new PlainIdentifier(key);
					}
				}
			}
		}
		return null;

	}

	public String getName() {
		return getName(getIdentifier());
	}

	public String getName(AbstractIdentifier identifier) {
		return identifier.toString();
	}

	public String getQualifiedName() {
		this.publicIdentifier = getPublicIdentifier();
		if (hasNamespace())
			return getNamespace() + "." + getName(publicIdentifier);
		else
			return getName(publicIdentifier);
	}

	public AbstractIdentifier getPublicIdentifier() {
		if (publicIdentifier == null)
			publicIdentifier = getIdentifier();
		return publicIdentifier;
	}

	public void setPublicIdentifier(AbstractIdentifier publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	private AbstractIdentifier getIdentifierFromObjectLiteralList(AbstractStatement statement) {
		for (ObjectLiteralExpression objectLiteralExpression : statement.getObjectLiteralExpressionList()) {
			if (objectLiteralExpression.equals(this))
				return IdentifierHelper.findLValue(statement, objectLiteralTree);
		}
		return null;
	}
}
