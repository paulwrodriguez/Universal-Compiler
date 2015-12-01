package SymbolTable;

import Debug.Debugger;

public class SymbolTable {
	// private Data memebers
	private StringSpace ss;
	private HashTable table;
	private SharedData sd;
	private int currentScopeNumber;
	
	// Constructor
	public SymbolTable(){
		ss = new StringSpace();
		sd = new SharedData();
		sd.ss = ss;
		sd.currentScopeNumber = currentScopeNumber;
		table = new HashTable(sd);
	}

	
	// Public  Member Functions
	public void create() {}
	public void destroy() {}
	public void enter(String name){
		if(Debugger.SYMBOLTABLE){
			System.out.println("symboltable.enter [" + name + "]");
		}
		sd.currentScopeNumber = currentScopeNumber;
		table.enter(name);
	}
	public boolean find(String name){
		boolean found = table.find(name);
		return found;
	}
	public void associate(){}
	public void retrieve(){}
	public String dump() {
		return table.dump();
	};
	
	public void incrementScope() {
		++currentScopeNumber;
		sd.currentScopeNumber = currentScopeNumber;
	}
	public void decrementScope(){
		--currentScopeNumber;
		sd.currentScopeNumber = currentScopeNumber;
	}
	public void removeCurrentScope() {
		table.removeCurrentScope();
	}
	public StringSpace getStringSpace() {
		return ss;
	}
	// Private Member Functions
	
	
	
	// Testing
	public void test() {
		SymbolTable st = new SymbolTable();
		st.create();
		st.enter("x");
	}


	@Override
	public String toString() {
		return "SymbolTable [ss=" + ss + ", table=" + table + "]";
	}



	
}
