package Data;

import Interfaces.SemanticStackable;

public class Token implements SemanticStackable {
	private String text;   // e.g. begin
	private int code; 	   // e.g. code
	private String symbol; // e.g. beginsym
	private StackableDataType myType = StackableDataType.TOKEN;
	
	public Token(String t, int c, String s){
		setText(t);
		setCode(c);
		setSymbol(s);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return  "[" + symbol + "]"; //"[text=" + text + ", code=" + code + ", symbol=" + symbol + "]";
	}

	@Override
	public boolean is(StackableDataType isThisType) {
		return myType == isThisType;
	}
	
	
}
