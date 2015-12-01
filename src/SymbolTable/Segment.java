package SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;

import Debug.Debugger;

public class Segment {
	public static final int SEGMENTSIZE = 500;
	private char[] stringspace; 
	private int lastIndex = 0; // marks the first unused index
	public static int NumOfSegments = 0;
	
	public Segment() {
		stringspace = new char[SEGMENTSIZE];
		NumOfSegments++;
	}
	public StringSpaceElement append(String name){
		if(Debugger.SYMBOLTABLE){
			System.out.println("segment append [ " + name + " ]");
		}
		StringSpaceElement sse = new StringSpaceElement();
		sse.startIndex = getStringSpaceIndexNumber();
		for(char c : name.toCharArray() ){
			insert(c);
		}
		sse.endIndex = getStringSpaceIndexNumber();
		if(Debugger.SYMBOLTABLE){
			int length = name.length();
			int startIndex = lastIndex - length;
			for(char c : name.toCharArray()){
				if( c != stringspace[startIndex]) {
					Debugger.ERRORNOTHROW("error with add " + c + " != " + stringspace[startIndex], Debugger.ENDPROGRAM);
				}
				startIndex++;
			}
		}
		return sse;
		
	}
	private int getStringSpaceIndexNumber(){
		return lastIndex + ((NumOfSegments-1) * SEGMENTSIZE);
	}
	private void insert(char c){
		stringspace[lastIndex] = c;
		lastIndex++;
	}
	public boolean isFit(String name){
		int spaceLeft = SEGMENTSIZE - lastIndex - 1;
		if(spaceLeft >= name.length()) {  
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "Segment [stringspace=" + Arrays.toString(stringspace)
				+ ", lastIndex=" + lastIndex + "]";
	}
	
	// Public Member Functions
	public String retrieve(StringSpaceElement element) {
		if(Debugger.SYMBOLTABLE){
			System.out.println("Segment retrieve " + element);
		}
		String result = "";
		for(int i = element.startIndex; i < element.endIndex; ++i){
			if(Debugger.SYMBOLTABLE){
				if(i >= lastIndex){
					Debugger.ERRORNOTHROW("Segment retrieve index error " + element, Debugger.FORDEBUG);
				}
			}
			result += stringspace[i];
		}
		if(Debugger.SYMBOLTABLE){
			System.out.println("segment retrieved " + result + " from segment stringspace"); 
		}
		return result;
	}
	public boolean remove(StringSpaceElement element) {
		if(element.startIndex >= lastIndex){
			Debugger.ERRORNOTHROW("segment.remove could not remove " + retrieve(element) + ". segment = " + this, Debugger.FORDEBUG);
			return false; // either has already been removed or invalid call to this function
		}
		if(element.alreadyPresent == true){
			return false;
		}
		lastIndex = element.startIndex;
		if(element.endIndex >= SEGMENTSIZE){
			Debugger.ERRORNOTHROW(" segment.remove endindex > segmentsize " + element.endIndex, Debugger.FORDEBUG);
		}
		return true;
	}
	public String dump() {
		String result = "";
		int i = 0;
		for(i =0; i < lastIndex; ++i){
			if(i == 0){
				result += "[";
			} else {
				result += "|";
			}
			result += stringspace[i];
		}
		if(i > 0){
			result += "]";
		}
		
		return result;
	}
	
}
