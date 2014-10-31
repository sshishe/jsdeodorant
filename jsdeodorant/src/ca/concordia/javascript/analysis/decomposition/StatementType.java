package ca.concordia.javascript.analysis.decomposition;

public enum StatementType {
	// Statements with body
	BLOCK,
	IF,
	WHILE,
	DO_WHILE,
	FOR_IN,
	FOR,
	FOR_OF,
	WITH,
	SWITCH,
	CASE_CLAUSE, 
	DEFAULT_CLAUSE, 
	TRY,
	CATCH, 
	LABELLED,
	// Statements without body
	VARIABLE,
	EMPTY,
	EXPRESSION,
	BREAK,
	CONTINUE,
	RETURN,
	THROW,
	DEBUGGER;
	
	public String toString() {
		return name().toLowerCase();
	}
}
