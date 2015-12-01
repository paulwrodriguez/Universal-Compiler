package Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import Debug.Debugger;


public class Production {
	private ArrayList<Symbol> symbols;
	
	public Production(){ symbols = new ArrayList<Symbol>(); }
	public Production(Production p){
		symbols = new ArrayList<Symbol>();
		symbols.addAll(p.getAllSymbols());
	}
	public Production(ArrayList<Symbol> r){
		symbols = new ArrayList<Symbol>();
		symbols.addAll(r);
	}
	public void set(int index, Symbol e){
		symbols.set(index, e);
	}

	public ArrayList<Symbol> getAllNoActionSymbols(){ 
		ArrayList<Symbol> result = new ArrayList<Symbol>(symbols);
		for(int i = 0; i < symbols.size(); ++i){
			if(symbols.get(i).isActionSymbol())
				result.remove(symbols.get(i));
			}
		return result; 
	}
	public ArrayList<Symbol> getAllSymbols() {
		return symbols;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Production p = (Production) obj;
		boolean result = true;
		if(p.size() != this.symbols.size()){
			result = false;
		}
		else {
			for(int i = 0; i < p.size(); ++i ){
				try {
					result = p.getSymbol(i).equals(this.getSymbol(i));
				} catch (ErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						Debugger.ERROR("ERROR: Production.Equals", Debugger.ENDPROGRAM);
					} catch (ErrorException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(result == false){
					break;
				}
			}
		}
		return result;
	}
	@Override
	public int hashCode(){
		return Objects.hash(this.symbols);
	}
	
	/*
	 * Deprecated. unknown effect with addtion of actionsymbol
	 * ok to be used in equals function as it will get every symbol
	 */
	private Symbol getSymbol(int i) throws Data.ErrorException{ 
		if(i > this.size() && i == this.size()){
			Debugger.ERROR("getSymbol index: " + i + " larger then size: " + this.size() , Debugger.TERMINATE);
		} 
		return symbols.get(i);
	}
	private int size() {
		return symbols.size();
	}

	
	public int contains(Symbol symbol) {
		int i = 0;
		for(Symbol s : symbols){
			if(s.equals(symbol)){
				return i;
			}
			++i;
		}
		return -1;
	}

	public void add(Symbol s){
		symbols.add(s);
	}
	
	@Override
	public String toString(){
		String rtn = "";
		for(Symbol s : symbols){
			rtn += s.getText();
			if(s.isLHS()){
				rtn +="->";
			}
		}
		return rtn;
	}
	
	public String getListOfSymbolDetails(){
		String rtn = "";
		for(Symbol s : symbols){
			rtn += s.toString() + "\n";
		}
		return rtn;
	}
	public int getLHSLength() throws ErrorException{
		if(symbols.size() < 1){
			Debugger.ERROR("production.getLHSLength < 1", Debugger.TERMINATE);
		}
		if(symbols.get(0).isNonTerminal() && !symbols.get(0).isTerminal()){
			return 1;
		}
		else{
			Debugger.ERROR("production.getLHSLength. symbol(0) != terminal || isnonTermainal", Debugger.TERMINATE);
			return -1;
		}
	}
	
	// Post: returns length without action symbols
	public int getRHSLengthNoActionSymbols() throws ErrorException{
		if(getLHSLength() != 1){
			Debugger.ERROR("production.getRHSLength ... getLHSLength != 1", Debugger.TERMINATE);
		}
		int count = 0;
		for(Symbol s : symbols){
			if(!s.isActionSymbol()){
				++count;
			}
		}
		return count - 1; // remove one for lhs
	}
	/*
	 * Pre: i is between 0 - n
	 * Post: if !0 then throws exception
	 */
	public Symbol getLHSSymbol() throws ErrorException{
		int i = 0; // 0 should always be LHS symbol
		if(!getSymbol(i).isLHS()){
			Debugger.ERRORNOTHROW("production getlhssymbol. index 0 != lhs symbol", Debugger.ENDPROGRAM);
		}
		return getSymbol(i);
	}
	/*
	 * Pre: i is inclusive 0 - size -2
	 */
	public Symbol getRHSSymbolNoAction(int i) throws ErrorException{
		if(i < 0){
			Debugger.ERROR("getRHSSymbol i < 0", Debugger.TERMINATE);
		}
		// i mapped from 0 - size- 2
		return getSymbolNoAction(i + 1); // should error handle
	}
	public Symbol getRHSSymbol(int i) throws ErrorException {
		if(i < 0){
			Debugger.ERROR("getRHSSymbol i < 0", Debugger.TERMINATE);
		}
		return symbols.get(i + 1);
	}
	/*
	 * Pre: i is inclusive 0 - size -2 
	 */
	public Symbol getSymbolNoAction(int i) throws ErrorException{
		if(i > this.size() && i == this.size()){
			Debugger.ERROR("getSymbol index: " + i + " larger then size: " + this.size() , Debugger.TERMINATE);
		} 
		
		ArrayList<Symbol> temp = new ArrayList<Symbol>(symbols);
		for(Symbol s : symbols){
			if(s.isActionSymbol()){
				temp.remove(s);
			}
		}
		return temp.get(i); 
	}

	public ArrayList<Symbol> getRHSNoAction() throws ErrorException {
		ArrayList<Symbol> temp = new ArrayList<Symbol>(symbols);
		if(!temp.get(0).isLHS()){
			Debugger.ERROR("production.getRHS != rhs", Debugger.TERMINATE);
		}
		temp.remove(0);
		if(temp.size() == 1){
			if(temp.get(0).equals(Symbol.LAMPDA)){
				temp.remove(0);
			}
		}
		for(Symbol s : symbols){
			if(s.isActionSymbol()){
				temp.remove(s);
			}
		}
		return temp;
	}
	private ArrayList<Symbol> removeActionSymbols(ArrayList<Symbol> temp){
		for(int i = 0; i < temp.size(); ++i){
			if(temp.get(i).isActionSymbol()){
				temp.remove(i);
				--i;
			}
		}
		return temp;
	}
	public ArrayList<Symbol> getRHSAfterNoActionSymbols(int i) {
		ArrayList<Symbol> temp = new ArrayList<Symbol>(this.getAllNoActionSymbols());
		if(Debugger.DEBUG){
			if(temp.get(0).isRHS()){
				Debugger.ERRORNOTHROW(" getrhsafter first element is not lhs", Debugger.ENDPROGRAM);
			}
		}
		// remove all action symbols
		// TODO remove this test after shown to be true
		ArrayList<Symbol> check = new ArrayList<Symbol>(symbols);
		removeActionSymbols(check);
		temp = removeActionSymbols(temp);
		temp.remove(0); // convert to rhs
		for(int j = 0; j <= i; j++){
			temp.remove(0);
		}
		
		
		return temp;
	}
	public int getRHSLength() {
		return symbols.size() -1;
	}
}
