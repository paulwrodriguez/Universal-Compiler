package SymbolTable;

public class StringSpaceElement {
	public int startIndex;
	public int endIndex;
	public int scope;
	public boolean alreadyPresent;
	public StringSpaceElement(){
		startIndex = 0;
		endIndex = 0;
		scope = 0;
		alreadyPresent = false;
	}
	
	public StringSpaceElement(StringSpaceElement other){
		this.startIndex = other.startIndex;
		this.endIndex = other.endIndex;
		this.scope = other.scope;
		this.alreadyPresent = other.alreadyPresent;
	}

	@Override
	public String toString() {
		return "StringSpaceElement [startIndex=" + startIndex + ", endIndex="
				+ endIndex + ", scope=" + scope + "]";
	}

	public String dump() {
		String result = "";
		result += "[" + startIndex + " | " + (endIndex - startIndex) + "][Scope" + scope + "]";
		return result;
	}

	public int length() {
		return endIndex - startIndex;
	}
	
	
}
