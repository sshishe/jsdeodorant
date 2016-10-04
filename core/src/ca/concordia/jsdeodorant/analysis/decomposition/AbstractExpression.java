package ca.concordia.jsdeodorant.analysis.decomposition;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;

import ca.concordia.jsdeodorant.analysis.abstraction.AbstractIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.CompositeIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.FunctionInvocation;
import ca.concordia.jsdeodorant.analysis.abstraction.Namespace;
import ca.concordia.jsdeodorant.analysis.abstraction.PlainIdentifier;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceContainer;
import ca.concordia.jsdeodorant.analysis.util.DebugHelper;
import ca.concordia.jsdeodorant.analysis.util.ExpressionExtractor;
import ca.concordia.jsdeodorant.analysis.util.IdentifierHelper;
import ca.concordia.jsdeodorant.analysis.util.ModelHelper;

public class AbstractExpression extends AbstractFunctionFragment implements CodeFragment{
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
		processAssignmentExpressions(expressionExtractor.getBinaryOperators(expression));
	}

	public AbstractExpression(ParseTree expression, SourceContainer parent, boolean processInside) {
		super(parent);
		this.expression = expression;
		if (processInside) {
			ExpressionExtractor expressionExtractor = new ExpressionExtractor();
			processFunctionInvocations(expressionExtractor.getCallExpressions(expression));
			processVariableDeclarations(expressionExtractor.getVariableDeclarationExpressions(expression));
			processNewExpressions(expressionExtractor.getNewExpressions(expression));
			processArrayLiteralExpressions(expressionExtractor.getArrayLiteralExpressions(expression));
			processAssignmentExpressions(expressionExtractor.getBinaryOperators(expression));
		}
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

	public Namespace getRawNamespace() {
		if (namespace == null) {
			SourceContainer parent = getParent();
			namespace = buildNamespaceStructure(parent);
		}
		return namespace;
	}

	public Namespace getNamespace() {
		if (getRawNamespace() != null)
			if (refineIdentifier())
				return namespace;
		return null;
	}

	public boolean hasNamespace() {
		return getNamespace() != null;
	}

	public boolean refineIdentifier() {
		if (namespace != null) {
			AbstractExpression part = namespace.getPart();
			if (part instanceof FunctionDeclarationExpression) {
				FunctionDeclarationExpression functionDeclarationExpression = (FunctionDeclarationExpression) part;
				if (functionDeclarationExpression.getFunctionDeclarationExpressionNature() == FunctionDeclarationExpressionNature.NEW_FUNCTION) {
					return refineIdentifierForNewFunctionWithThis();
				}
				if (functionDeclarationExpression.getFunctionDeclarationExpressionNature() == FunctionDeclarationExpressionNature.IIFE) {
					// IIFE with passing parameter -> (function(namespace){namespace.publicFunction=...})(namespace);
					if (functionDeclarationExpression.getParameters().size() > 0) {
						return refineIdentifierForIIFEWithParamter(functionDeclarationExpression);
					}
					AbstractStatement statement = ModelHelper.getStatementContainingFunctionDeclaration(functionDeclarationExpression);
					List<FunctionInvocation> functionInvocations = statement.getFunctionInvocationList();
					for (FunctionInvocation functionInvocation : functionInvocations) {
						if (functionInvocation.getOperand().expression instanceof MemberExpressionTree) {
							MemberExpressionTree memberExpression = functionInvocation.getOperand().expression.asMemberExpression();
							if (memberExpression.operand instanceof ParenExpressionTree)
								if (memberExpression.operand.asParenExpression().expression.equals(functionDeclarationExpression.getFunctionDeclarationTree()))
									if (memberExpression.memberName.value.equals("apply")) {
										return refineIdentifierForIIFEWithApplyMethod(functionDeclarationExpression, functionInvocation.getArguments());
									}
						}
					}
					// IIFE which return statement is used to expose members either using return localVar or return {};
					List<AbstractStatement> returnStatements = functionDeclarationExpression.getReturnStatementList();
					if (returnStatements != null)
						if (this instanceof IdentifiableExpression) {
							AbstractIdentifier identifier = this.asIdentifiableExpression().getIdentifier();
							for (AbstractStatement returnStatement : returnStatements) {
								// return localVar;
								if (returnStatement.getStatement().asReturnStatement().expression instanceof IdentifierExpressionTree) {
									return refineIdentifierForIIFEWithReturnVariable(identifier, returnStatement);
									// return {};
								} else if (returnStatement.getStatement().asReturnStatement().expression instanceof ObjectLiteralExpressionTree) {
									return refineIdentifierForIIFEWithReturnObjectLiteral(returnStatement);
								}
							}
						}
				}
			}

			if (part instanceof ObjectLiteralExpression) {
				ObjectLiteralExpression objectLiteralExpression = (ObjectLiteralExpression) part;
				if (objectLiteralExpression.getIdentifier() != null) {
					if (this instanceof IdentifiableExpression) {
						return refineIdentifierForObjectLiteral();
					}
				}
			}
		}
		return false;
	}

	/**
	 * Exposing public member by assigning members to this which this is bind by
	 * apply
	 * 
	 * @param functionDeclarationExpression
	 * @param arguments
	 * @return
	 */
	private boolean refineIdentifierForIIFEWithApplyMethod(FunctionDeclarationExpression functionDeclarationExpression, List<AbstractExpression> arguments) {
		// apply accepts one argument
		AbstractExpression argument = arguments.get(0);
		if (this instanceof IdentifiableExpression) {
			AbstractIdentifier identifier = this.asIdentifiableExpression().getIdentifier();
			if (identifier instanceof CompositeIdentifier) {
				PlainIdentifier mostLeftPart = identifier.asCompositeIdentifier().getMostLeftPart();
				// variable declarations are not accessible outside of new function(){} unless they assigned to this
				if (mostLeftPart.getIdentifierName().equals("this")) {
					AbstractIdentifier leftPart = IdentifierHelper.getIdentifier(argument.getExpression());
					this.asIdentifiableExpression().setPublicIdentifier(new CompositeIdentifier(leftPart, identifier.asCompositeIdentifier().getRightPart()));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * They way public members are exposed: using object literal var
	 * namespace={publicFunction:function(){},anotherPublicFunction:function(){}
	 * };
	 * 
	 * @return
	 */
	private boolean refineIdentifierForObjectLiteral() {
		AbstractIdentifier identifier = this.asIdentifiableExpression().getIdentifier();
		//this.asIdentifiableExpression().setPublicIdentifier(new CompositeIdentifier(objectLiteralExpression.getIdentifier().getNode(), identifier));
		this.asIdentifiableExpression().setPublicIdentifier(identifier);
		return true;
	}

	/**
	 * new function(){this.publicFunction = function() {};}
	 * 
	 * @return
	 */
	private boolean refineIdentifierForNewFunctionWithThis() {
		//if we add more classes extending AbstractExpression but the given class does not implement IdentifiableExpression
		if (this instanceof IdentifiableExpression) {
			AbstractIdentifier identifier = this.asIdentifiableExpression().getIdentifier();
			if (identifier instanceof CompositeIdentifier) {
				PlainIdentifier mostLeftPart = identifier.asCompositeIdentifier().getMostLeftPart();
				// variable declarations are not accessible outside of new function(){} unless they assigned to this
				if (mostLeftPart.getIdentifierName().equals("this")) {
					this.asIdentifiableExpression().setPublicIdentifier(((CompositeIdentifier) identifier).getRightPart());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * They way public members are exposed: return
	 * {publicMethod1:privateMethod1,
	 * properties:{publicProperty1:privateProperty1}}
	 * 
	 * @param returnStatement
	 * @return
	 */
	private boolean refineIdentifierForIIFEWithReturnObjectLiteral(AbstractStatement returnStatement) {
		for (ObjectLiteralExpression objectLiteralExpression : returnStatement.getObjectLiteralExpressionList()) {
			Map<Token, AbstractExpression> propertyMap = objectLiteralExpression.getPropertyMap();
			for (Token key : propertyMap.keySet()) {
				AbstractExpression abstractExpression = propertyMap.get(key);
				if (IdentifierHelper.getIdentifier(abstractExpression.expression).toString().equals(this.asIdentifiableExpression().getIdentifier().toString())) {
					this.asIdentifiableExpression().setPublicIdentifier(new PlainIdentifier(key));
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * The way public members are exposed using returning local variable: var
	 * instance = {}; instance.publicMethod=...; return instance;
	 * 
	 * @param identifier
	 * @param returnStatement
	 * @return
	 */
	private boolean refineIdentifierForIIFEWithReturnVariable(AbstractIdentifier identifier, AbstractStatement returnStatement) {
		if (identifier instanceof CompositeIdentifier) {
			PlainIdentifier mostLeftPart = identifier.asCompositeIdentifier().getMostLeftPart();
			if (mostLeftPart.getNode() instanceof IdentifierExpressionTree) {
				if (returnStatement.getStatement().asReturnStatement().expression.asIdentifierExpression().identifierToken.value.toString().equals(mostLeftPart.getNode().asIdentifierExpression().identifierToken.value.toString())) {
					this.asIdentifiableExpression().setPublicIdentifier(((CompositeIdentifier) identifier).getRightPart());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * They way public members are exposed using parameter: (function
	 * (namespace){ namespace.publicMethod=function(){...};}
	 * 
	 * @param functionDeclarationExpression
	 * @return
	 */
	private boolean refineIdentifierForIIFEWithParamter(FunctionDeclarationExpression functionDeclarationExpression) {
		AbstractIdentifier identifier = this.asIdentifiableExpression().getIdentifier();
		if (identifier instanceof CompositeIdentifier) {
			PlainIdentifier mostLeftPart = identifier.asCompositeIdentifier().getMostLeftPart();
			if (mostLeftPart.getNode() instanceof IdentifierExpressionTree) {
				for (AbstractExpression parameter : functionDeclarationExpression.getParameters()) {
					if (parameter.expression.asIdentifierExpression().identifierToken.value.equals(mostLeftPart.getNode().asIdentifierExpression().identifierToken.value)) {
						// The public identifier is equal to internal identifier
						return true;
					}

				}
			}
		}
		return false;
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
			Namespace parentNamespace = new Namespace(parentExpression);
			if (namespace == null)
				this.namespace = namespace = parentNamespace;
			else
				namespace = namespace.setParent(parentNamespace);
			if (parentExpression.getParent() != null)
				buildNamespaceStructure(parentExpression.getParent(), namespace);
		}
		return this.namespace;
	}

	public boolean isIdentifiableExpression() {
		if (this instanceof IdentifiableExpression)
			return true;
		return false;
	}

	public IdentifiableExpression asIdentifiableExpression() {
		if (this instanceof IdentifiableExpression)
			return (IdentifiableExpression) this;
		else
			throw new ClassCastException("This class is not of type IdentifiableExpression");
	}
}
