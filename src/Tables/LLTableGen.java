package Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Data.Symbol;
import Data.TermSet;
import Debug.Debugger;

public class LLTableGen {

	private ArrayList<Symbol> vocabulary;
	private ArrayList<Symbol> lhs;
	private ArrayList<TermSet> predictSet;
	private int [][] table;
	private boolean isGenerated;
	private static final int NOENTRY = 0;

	
	public LLTableGen() {
		setGenerated(false);
	}
	
	
	/* 
	 * Setters and Getters
	 */


	public LLTableGen(ArrayList<Symbol> lhs,ArrayList<TermSet> predictSet, HashSet<Symbol> terminals) {
		this.lhs = lhs;
		this.predictSet = predictSet;
		vocabulary = new ArrayList<Symbol>(terminals);
		isGenerated = false;
	}

	/*
	 * Pre: X is a symbol in the grammar
	 * 		a is a terminal symbol in the grammar
	 * Post: returns the production number stored at table intersection
	 * 		returns -1 if there is no entry and table intersection
	 */
	public int getEntry(Symbol X, Symbol a){
		int result = NOENTRY;
		int ntindex = lhs.indexOf(X);
		if(ntindex == -1){
			return -1;
//			Debugger.ERRORNOTHROW("lltable getentry ntindex=-1", Debugger.ENDPROGRAM);
		}
		int tindex = vocabulary.indexOf(a);
		if(tindex == -1){
			return -1;
//			Debugger.ERRORNOTHROW("lltable getentry tindex=-1", Debugger.ENDPROGRAM);
		}
		for(int i = 0; i < lhs.size(); ++i){
			if(lhs.get(i).equals(X))
				if(table[i][tindex] != NOENTRY)
					return table[i][tindex];
		}
		return result;
	}

	public ArrayList<Symbol> getVocabulary() {
		return vocabulary;
	}
	public boolean isGenerated() {
		return isGenerated;
	}

	public void setGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

	private int getNonTerminalProductionNumber(int i){
		return i + 1;
	}
	public void generate() {
		table = new int[lhs.size()][vocabulary.size()];
		for(int i = 0; i < lhs.size(); ++i){
			TermSet symbols = predictSet.get(i);
			for(int j = 0; j < vocabulary.size(); ++j){
				/*
				 * if symbols.contains(v)
				 * 	table[nont.index][i] = nonterm.production number
				 */
				if(symbols.contains(vocabulary.get(j))){
					table[i][j] = getNonTerminalProductionNumber(i);
				}
			}
		}
	}
	
	@Override
	public String toString(){
		String result = "";
		
		for(int i = 0; i < table.length; ++i){
			for(int j = 0; j < table[i].length; ++j){
				if(table[i][j] != 0){
					result += "[" + lhs.get(i).getText() + "][" + vocabulary.get(j).getText() + "] =" + table[i][j];
				}
			}
			result+="\n";
		}
		return result;
	}
	
	
	

}
