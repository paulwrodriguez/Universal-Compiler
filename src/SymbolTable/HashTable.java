package SymbolTable;

import java.math.BigInteger;
import java.util.ArrayList;

import Debug.Debugger;

public class HashTable {

	// Private variables
	private ArrayList<SymbolList> table;
	private static final int HASHTABLESIZE = 577;
	private SharedData sd;
	
	// Public vairbales
	
	public HashTable(SharedData _sd) {
		table = new ArrayList<SymbolList>();
		sd = _sd;
		for(int i = 0; i < HASHTABLESIZE; ++i){
			table.add(new SymbolList(sd));
		}
	}
	
	// Public Member Functions
	
	public void enter(String name) {
		int index = hash(name);
		table.get(index).push(name);
	}
	
	public boolean find(String name){
		boolean found = false;
		int index = hash(name);
		found = table.get(index).contains(name);
		if(Debugger.SYMBOLTABLE){
			System.out.println("Hastable find " + name + " = " + found); 
		}
		return found;
	}
	
	public void removeCurrentScope() {
		if(Debugger.SYMBOLTABLE){
			System.out.println("HashTable.removeCurrentScope. CurrentScope = " + sd.currentScopeNumber);
		}
		for(SymbolList sl : table){
			sl.removeScope(sd.currentScopeNumber);
		}
		
	}
	
	public String dump() {
		String result = "";
		for(SymbolList sl : table){
			if(sl.length() > 0){
//				result += sl.dump() + "\n";
				result += String.format("%1$23s %2$s\n","", sl.dump());
			}
		}
		return result;
	}
	// Private Memeber Functions
	
	private int hash(String name) {
		BigInteger bi = new BigInteger("1");
		for(char c : name.toCharArray()){
			bi = bi.multiply((BigInteger.valueOf((long)c)));
		}
		BigInteger largePrime = new BigInteger("982451653");
		bi = bi.multiply(largePrime);
		if(Debugger.SYMBOLTABLE){
			System.out.println("hastable hash [" + bi + " ]");
		}
		return bi.mod(BigInteger.valueOf((long)HASHTABLESIZE)).intValue();
		
	}




	
}
