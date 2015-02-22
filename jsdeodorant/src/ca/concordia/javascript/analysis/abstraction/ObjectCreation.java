package ca.concordia.javascript.analysis.abstraction;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.decomposition.FunctionDeclaration;
import ca.concordia.javascript.analysis.util.QualifiedNameExtractor;
import ca.concordia.javascript.analysis.util.SourceHelper;

public class ObjectCreation extends Creation {
	static Logger log = Logger.getLogger(ObjectCreation.class.getName());
	private NewExpressionTree newExpressionTree;
	private FunctionDeclaration classDeclaration;
	private ClassDeclarationType classDeclarationType;
	private AbstractExpression operandOfNew;
	private List<AbstractExpression> arguments;
	private QualifiedName qualifiedName;

	public ObjectCreation(NewExpressionTree newExpressionTree) {
		this.newExpressionTree = newExpressionTree;
	}

	/**
	 * 
	 * @param newExpressionTree
	 * @param operandOfNew
	 *            the name of class that would instantiate
	 * @param arguments
	 *            passed to constructor
	 */
	public ObjectCreation(NewExpressionTree newExpressionTree,
			AbstractExpression operandOfNew, List<AbstractExpression> arguments) {
		this.newExpressionTree = newExpressionTree;
		this.operandOfNew = operandOfNew;
		this.arguments = arguments;
	}

	public FunctionDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(ClassDeclarationType classDeclarationType) {
		setClassDeclaration(classDeclarationType, null);
	}

	public void setClassDeclaration(ClassDeclarationType classDeclarationType,
			FunctionDeclaration functionDeclaration) {
		this.classDeclarationType = classDeclarationType;
		this.classDeclaration = functionDeclaration;
	}

	public AbstractExpression getOperandOfNew() {
		return operandOfNew;
	}

	public String getClassName() {
		return QualifiedNameExtractor.getQualifiedName(
				operandOfNew.getExpression()).toString();
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

	public ClassDeclarationType getClassDeclarationType() {
		return classDeclarationType;
	}

	public QualifiedName getNamespace() {
		return qualifiedName;
	}

	public void setNamespace(QualifiedName namespace) {
		this.qualifiedName = namespace;
	}

	public String toString() {
		return SourceHelper.extract(newExpressionTree);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ObjectCreation) {
			ObjectCreation toCompare = (ObjectCreation) other;
			return Objects.equals(this.newExpressionTree,
					toCompare.newExpressionTree);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(newExpressionTree);
	}
}
