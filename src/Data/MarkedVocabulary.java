package Data;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkedVocabulary {
	public HashMap<Symbol, Boolean> v;
	
	public MarkedVocabulary(){ v = new HashMap<Symbol, Boolean>(); }
	public void put(Symbol s, boolean val){
		v.put(s, val);
	}
	public Boolean get(Symbol symbol){
		return v.get(symbol);
	}
	public int size(){
		return v.size();
	}
	public HashMap<Symbol, Boolean> getMap() {
		return v;
	}
}
