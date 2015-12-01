package Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import Debug.Debugger;

/* Create Grammar help classes*/
/*
 * Symbol
 * Production
 * Grammar
 * 
 */

public class Grammar {
	private ArrayList<Production> productions;
	private ArrayList<Symbol> vocabulary;
	private HashSet<Symbol> nonterminals;
	private HashSet<Symbol> terminals;
	private Symbol starting_symbol;

	/*
	 * Constructor
	 */
	public Grammar() {
		productions = new ArrayList<Production>();
		vocabulary = new ArrayList<Symbol>();
		nonterminals = new HashSet<Symbol>();
		terminals = new HashSet<Symbol>();
		starting_symbol = new Symbol();
	}

	public Symbol getStarting_symbol() {
		return starting_symbol;
	}

	public void setStarting_symbol(String ss) {
		this.starting_symbol = new Symbol(true, false, false, true, ss);
	}

	/*
	 * Override functions
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grammar g = (Grammar) obj;

		boolean result = true;
		// TODO remove this
		if (g.size() != this.size()) {
			Debugger.ERRORNOTHROW("grammar equlas g.size: " + g.size()
					+ " != this.size(): " + this.size(), Debugger.ENDPROGRAM);
		} else {
			for (int i = 0; i < this.size(); ++i) {
				try {
					result = this.getProduction(i).equals(g.getProduction(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Debugger.ERRORNOTHROW(
							"grammar equals .production.equals threw",
							Debugger.ENDPROGRAM);
					e.printStackTrace();
				}
				if (result == false) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getProductions());
	}

	/*
	 * Getters and Setters
	 */
	public ArrayList<Symbol> getVocabulary() {
		return vocabulary;
	}

	public void extractVocabularyFromGrammer() {
		HashSet<Symbol> temp = new HashSet<Symbol>(getAllSymbols());
		vocabulary = new ArrayList<Symbol>(temp);
	}

	public ArrayList<Production> getProductions() {
		return productions;
	}

	public ArrayList<Symbol> getAllSymbols() {
		ArrayList<Symbol> temp = new ArrayList<Symbol>();
		for (int i = 0; i < this.getProductions().size(); ++i) {
			temp.addAll(getProductions().get(i).getAllNoActionSymbols());
		}
		return temp;
	}

	public Production getProduction(int index) throws Exception {
		if (productions.size() > index) {
			return productions.get(index);
		} else {
			Debugger.ERROR("grammar.getproduction.size < " + index,
					Debugger.TERMINATE);
			return null;
		}
	}

	public int size() {
		return productions.size();
	}

	public String getProductionAndSymbolDetails() {
		String rtn = "";
		int i = 1;
		for (Production p : productions) {
			rtn += i++ + ") " + p.toString();
			rtn += "\n" + p.getListOfSymbolDetails();
		}
		return rtn;
	}

	public void add(Production p) {
		productions.add(p);
	}

	@Override
	public String toString() {
		String rtn = "";
		int i = 0;
		for (Production p : productions) {
			rtn += ++i + ") " + p.toString() + "\n";

		}
		return rtn;
	}

	/*
	 * Pre: vocabulary is filled with all symbols. not duplicates
	 */
	public void extractNonTerminals() {
		for (Symbol s : vocabulary) {
			if (s.isNonTerminal()) {
				nonterminals.add(s);
			}
		}
	}

	public void extractTerminals() {
		for (Symbol s : vocabulary) {
			if (s.isTerminal()) {
				terminals.add(s);
			}
		}
	}

	public HashSet<Symbol> getNonTerminals() {
		return nonterminals;
	}

	public HashSet<Symbol> getTerminals() {
		return terminals;
	}

	public ArrayList<Production> getNonTerminalProductionList(Symbol a)
			throws ErrorException {
		ArrayList<Production> temp = new ArrayList<Production>();
		for (Production p : productions) {
			if (p.getLHSSymbol().equals(a)) {
				temp.add(p);
			}
		}
		return temp;
	}
	public ArrayList<Symbol> getAllLHS() throws ErrorException{
		ArrayList<Symbol> lhs = new ArrayList<Symbol>();
		for(Production p : productions){
			lhs.add(p.getLHSSymbol());
		}
		return lhs;
	}

	public Production getProductionByNumber(int t) throws Exception {
		return getProduction(t - 1);
	}

}
