package Data;

import java.util.Objects;

import Debug.Debugger;
import Interfaces.ParseStackable;
import Interfaces.SemanticStackable;


public class Symbol implements ParseStackable, SemanticStackable{
	public static final Symbol LAMPDA = new Symbol(false, true, false, false, "lampda");
	public static final Symbol EMPTYSET = new Symbol(false, false, false, false, "emptySet");
	
	// for contructor
	public static final int TERMINALSYMBOL= 1;
	public static final int NONTERMINALSYMBOL_R = 2;
	public static final int NONTERMINALSYMBOL_L = 3;
	public static final int ACTIONSYMBOL = 4;
	
	private boolean LHS;
	private boolean RHS;
	private boolean Terminal;
	private boolean NonTerminal;
	private boolean ActionSymbol;
	private String text;
	private StackableDataType myType;
	/* implements parsestackable*/
	@Override
	public boolean is(StackableDataType isThisType) {
		// TODO Auto-generated method stub
		return myType == isThisType;
	}
	
	private void setStackableDataType() {
//		if(Debugger.DEBUG)
//			if(Terminal == NonTerminal == true || LHS == RHS == true)
//				if(!this.text.equalsIgnoreCase("lampda") && !this.text.equalsIgnoreCase("emptySet")) 
//					Debugger.ERRORNOTHROW("ERROR with symbol" + this.toString(), Debugger.ENDPROGRAM);
		if(Terminal)
			myType = StackableDataType.TERMINAL;
		if(NonTerminal)
			myType = StackableDataType.NONTERMINAL;
		if(ActionSymbol)
			myType = StackableDataType.ACTIONSYMBOL;
	}
	
	public boolean isLHS() {
		return LHS;
	}
	public void setLHS(boolean lHS) {
		LHS = lHS;
	}
	public boolean isRHS() {
		return RHS;
	}
	public void setRHS(boolean rHS) {
		RHS = rHS;
	}
	public boolean isTerminal() {
		return Terminal;
	}
	public void setTerminal(boolean terminal) {
		Terminal = terminal;
		setStackableDataType();
	}
	public boolean isNonTerminal() {
		return NonTerminal;
	}
	public void setNonTerminal(boolean nonTerminal) {
		NonTerminal = nonTerminal;
		setStackableDataType();
	}
	

	public Symbol (){
		LHS = false;
		RHS = false;
		Terminal = false;
		NonTerminal = false;
		setText("");
		setStackableDataType();
	}
	public void test(){
		Symbol temp = new Symbol(true, false, false,false, "test");
		int one = temp.hashCode();
		temp = new Symbol(true, false, false, false, "test");
		int two = temp.hashCode();
		System.out.println(one + " " + two);
		
	}
	public Symbol (boolean lhs, boolean rhs, boolean terminal, boolean nonterminal, String text){
		LHS = lhs;
		RHS = rhs;
		Terminal = terminal;
		NonTerminal = nonterminal;
		setText(text);
		setActionSymbol(false);
		setStackableDataType();
	}	
	public Symbol (int symbolType, String txt){
		if(symbolType == TERMINALSYMBOL){
			LHS = false;
			Terminal = true;
			RHS = !LHS;
			NonTerminal = !Terminal;
			setActionSymbol(false);
		}
		if(symbolType == NONTERMINALSYMBOL_L){
			LHS = true;
			RHS = !LHS;
			NonTerminal = true;
			Terminal = !NonTerminal;
			setActionSymbol(false);
		}
		if(symbolType == NONTERMINALSYMBOL_R){
			LHS = false;
			RHS = !LHS;
			NonTerminal = true;
			Terminal = !NonTerminal;
			setActionSymbol(false);
		}
		if(symbolType == ACTIONSYMBOL){
			LHS = false;
			RHS = !LHS;
			NonTerminal = false;
			Terminal = false;
			setActionSymbol(true);
		}
		text = txt;
		setStackableDataType();
	}
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbol other = (Symbol) obj;

		if(other.LHS == this.LHS && 
				other.RHS == this.RHS &&
				other.NonTerminal == this.NonTerminal &&
				other.Terminal == this.Terminal &&
				other.text.equalsIgnoreCase(this.text) &&
				other.ActionSymbol == this.ActionSymbol) {
			return true; 	
		}
		else if( other.text.equalsIgnoreCase(this.text) &&
				other.NonTerminal == this.NonTerminal &&
				other.Terminal == this.Terminal ){
			if(other.LHS == this.RHS || other.RHS == this.LHS){ // TODO this should be dedundant. can remove if tested
				return true;
			}
		} 
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.text); // TODO is this good?
	}
	@Override
	public String toString(){
		String rtn = "";
//		rtn = "Symbol:"+ text + "\n\tLHS:" + LHS + "\n\tRHS:" + RHS + "\n\tTerminal:" + Terminal + "\n\tNonTerminal:" + NonTerminal + "\n\tActionSymbol:" + ActionSymbol ;
		rtn = "["  + text + "]";
		return rtn;
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	public boolean isActionSymbol() {
		return ActionSymbol;
	}
	public void setActionSymbol(boolean actionSymbol) {
		ActionSymbol = actionSymbol;
		setStackableDataType();
	}
	
	
}