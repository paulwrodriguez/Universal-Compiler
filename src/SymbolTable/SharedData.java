package SymbolTable;

public class SharedData {
	
	// Shareable Data Members
	public StringSpace ss;
	public int currentScopeNumber;
	
	public SharedData() {
		ss = null;
		currentScopeNumber = 0;
	}
}
