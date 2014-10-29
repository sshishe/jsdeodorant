package ca.concordia.javascript.analysis.abstraction;

public enum StatementType {
	EMPTYS,
	BLOCK,
	EXPRESSIONS,
	IF,
	LABElED,
	BREAK,
	CONTINUE,
	WITH,
	SWITCH,
	RETURN,
	THROW,
	TRY,
	WHILE,
	DOWHILE,
	FOR,
	FOR_IN,
	FOR_OF,
	LET,
	DEBUGGER;
	
	public String toString() {
		return name().toLowerCase();
	}
}
