package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedList;

public class SymbolList {
	// Private Variables
	private StringSpace ss;
	private LinkedList<StringSpaceElement> elementList;
	private SharedData sd;
	
	// Public Member Functions
	public SymbolList(SharedData _sd) {
		elementList = new LinkedList<StringSpaceElement>();
		sd = _sd;
		ss  = sd.ss;
	}
	public void push(String name) {
		for(StringSpaceElement element : elementList){
			if(element.length() == name.length()){
				if(ss.retrieve(element).equalsIgnoreCase(name)) {
					StringSpaceElement newElement = new StringSpaceElement(element);
					newElement.alreadyPresent = true;
					newElement.scope = sd.currentScopeNumber;
					elementList.push(newElement);
					return;
				}
			}
		}
		StringSpaceElement element = ss.enter(name);
		element.scope = sd.currentScopeNumber;
		elementList.push(element);
	}
	
	public boolean contains(String name){
		boolean found = false;
		for(StringSpaceElement element : elementList){
			if(ss.retrieve(element).equalsIgnoreCase(name)){
				found = true;
			}
		}
		return found;
	}
	public void removeScope(int scopeNumber) {
		ArrayList<StringSpaceElement> toRemove = new ArrayList<StringSpaceElement>();
		for(StringSpaceElement element : elementList){
			if(element.scope == scopeNumber){
				if(element.alreadyPresent == false){
					ss.remove(element); // remove from stringspace if it is the only copy that is needed
				}
				toRemove.add(element);
			}
			else {
				break;
			}
		}
		elementList.removeAll(toRemove);
	}
	
	public int length() {
		return elementList.size();
	}
	
	public String dump(){
		String result = "";
		for(StringSpaceElement element : elementList){
			result += "[" + ss.retrieve(element) + " | " + element.scope + "]";
		}
		return result;
		
	}
	
	// Private Member Functions
}
