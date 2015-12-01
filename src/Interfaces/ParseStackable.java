package Interfaces;


public interface ParseStackable {
	public enum StackableDataType {
		EOP, 
		NONTERMINAL,
		TERMINAL, 
		ACTIONSYMBOL,
		TOKEN, 
		SEMANTICRECORD
	}
	boolean is(StackableDataType isThisType);
}
