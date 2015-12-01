package Data;

import java.util.HashSet;

import Debug.Debugger;

public class TermSet {
	HashSet<Symbol> terms;
	
	public TermSet(){
		terms = new HashSet<Symbol>();
	}
	public TermSet(TermSet ts){
		terms = new HashSet<Symbol>();
		add(ts);
	}
	public TermSet(Symbol s) {
		terms = new HashSet<Symbol>();
		terms.add(s);
	}
	public HashSet<Symbol> getSet(){ return terms; }
	public TermSet add(Symbol s){
		if(terms.contains(Symbol.EMPTYSET)){
			terms.remove(Symbol.EMPTYSET);
		}
		terms.add(s);
		return this;
	}
	public void remove(Symbol s){
		terms.remove(s);
	}
	public int size(){ return terms.size(); }
	public TermSet add(TermSet t) {
		for(Symbol s : t.getSet()){
			add(s);
		}
		return this;
	}
	@Override
	public String toString(){
		String str = "{";
		for(Symbol s : terms){
			str += s.getText() + ",";
		}
		str+="}";
		
		return str;
	}
	public TermSet setSubstract(Symbol s) {
		TermSet result = new TermSet(this);
		result.remove(s);
		return result;
		
	}
	public boolean contains(Symbol s) {
		boolean var = terms.contains(s);
		if(Debugger.DEBUG){
			for(Symbol ss: terms){
				if(ss.equals(s)){
					if(var != true){
						Debugger.ERRORNOTHROW("termset contains not working", Debugger.ENDPROGRAM);
					}
				}
			}
		}
		return terms.contains(s);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((terms == null) ? 0 : terms.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermSet other = (TermSet) obj;
		if (terms == null) {
			if (other.terms != null)
				return false;
		} else if (!terms.equals(other.terms))
			return false;
		return true;
	}
	public TermSet setAdd(TermSet other) {
		TermSet result = new TermSet(this);
		result.add(other);
		return result;
	}
	public boolean containsAll(TermSet other) {
		boolean result = false;
		if(this.terms.containsAll(other.terms)){
			result = true;
		}
		return result;
	}
}
