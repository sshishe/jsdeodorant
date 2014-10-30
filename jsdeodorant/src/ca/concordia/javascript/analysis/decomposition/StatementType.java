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
	TRY,
	CATCH, //
	LABElED,
	LET,
	// Statements without body
	VARIABLE,
	EMPTY,
	EXPRESSION,
	BREAK,
	CONTINUE,
	RETURN,
	CASE_CLAUSE, //
	DEFAULT_CLAUSE, //
	THROW,
	DEBUGGER;
	
	public String toString() {
		return name().toLowerCase();
	}
}
