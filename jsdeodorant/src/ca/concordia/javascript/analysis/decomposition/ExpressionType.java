package ca.concordia.javascript.analysis.decomposition;

public enum ExpressionType {
	THIS,
	ARRAY,
	OBJECT,
	FUNCTION,
	ARROW,
	SEQUENCE,
	UNARY,
	BINARY,
	ASSIGNMENT,
	UPDATE,
	LOGICAL,
	CONDITIONAL,
	NEW,
	CALL,
	MEMBER,
	// Spider Monkey specific expressions
	YIELD,
	COMPREHENSION,
	GENERATOR,
	GRAPH,
	GRAPHINDEX,
	LET,
	
}
