package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedList;

import Debug.Debugger;

public class StringSpace {
	private ArrayList<Segment> segmentList;
	private LinkedList<StringSpaceElement> stringSpaceElementList;
	private int currentSegment;
	
	public StringSpace() {
		this.segmentList = new ArrayList<Segment>();
		this.segmentList.add(new Segment());
		this.stringSpaceElementList = new LinkedList<StringSpaceElement>();
	}
	
	/*
	 * add
	 * remove
	 * find (stringsegmentstring element
	 */
	public StringSpaceElement enter(String name){
		if(Debugger.SYMBOLTABLE){
			System.out.println("stringspace add [ " + name + " ]");
		}
		if(name.length() > Segment.SEGMENTSIZE) {
			Debugger.ERRORNOTHROW("stringspaceelement enter name_length > segmentSize. [" + name.length() + " > " + Segment.SEGMENTSIZE + "]", Debugger.ENDPROGRAM);
		}
		Segment i = segmentList.get(segmentList.size() - 1);
		if(i.isFit(name)){
			StringSpaceElement sse = i.append(name);
			stringSpaceElementList.push(sse);
			return sse;
		}
		else {
			addSegment();
			return enter(name);
		}
	}
	private void addSegment() {
		segmentList.add(new Segment());
	}
	public void remove() {}
	public void find(){}

	@Override
	public String toString() {
		return "StringSpace [segmentList=" + segmentList
				+ ", stringSpaceElementList=" + stringSpaceElementList
				+ ", currentSegment=" + currentSegment + "]";
	}

	// Public Member Functions
	public String retrieve(StringSpaceElement element) {
		if(Debugger.SYMBOLTABLE){
			System.out.println("stringspace retrieve " + element);
		}
		int selector = element.startIndex / Segment.SEGMENTSIZE;
		if(Debugger.SYMBOLTABLE){
			System.out.println("selector = " + selector);
		}
		return segmentList.get(selector).retrieve(element);
	}

	public void remove(StringSpaceElement element) {
		boolean isRemoved = stringSpaceElementList.remove(element);
		if(!isRemoved){
			Debugger.ERRORNOTHROW("Did not remove " + retrieve(element) + " ssel = " + stringSpaceElementList, Debugger.FORDEBUG);
		}
		int selector = element.startIndex / Segment.SEGMENTSIZE;
		segmentList.get(selector).remove(element);
	}

	public String dump() {
		String result = "";
		for(Segment s : segmentList){
			result += s.dump();
		}
		return result;
	}

	
	
	
	
}
