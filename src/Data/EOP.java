package Data;

import Interfaces.ParseStackable;

public class EOP implements ParseStackable{
	private int leftIndex;
	private int rightIndex;
	private int currentIndex;
	private int TopIndex;
	
	private StackableDataType myType = StackableDataType.EOP;
	
	public static int extractLeftIndex(ParseStackable p){
		EOP e = (EOP)p;
		return e.getLeftIndex();
	}
	public static int extractRightIndex(ParseStackable p){
		EOP e = (EOP)p;
		return e.getRightIndex();
	}
	public static int extractCurrentIndex(ParseStackable p){
		EOP e = (EOP)p;
		return e.getCurrentIndex();
	}
	public static int extractTopIndex(ParseStackable p){
		EOP e = (EOP)p;
		return e.getTopIndex();
	}
	@Override
	public boolean is(StackableDataType thisType) {
		return myType == thisType;
	}
		
	public EOP(int l, int r, int c, int t) {
		setLeftIndex(l);
		setRightIndex(r);
		setCurrentIndex(c);
		setTopIndex(t);
	}
	public int getRightIndex() {
		return rightIndex;
	}
	public void setRightIndex(int rightIndex) {
		this.rightIndex = rightIndex;
	}
	public int getCurrentIndex() {
		return currentIndex;
	}
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	public int getTopIndex() {
		return TopIndex;
	}
	public void setTopIndex(int topIndex) {
		TopIndex = topIndex;
	}
	public int getLeftIndex() {
		return leftIndex;
	}
	public void setLeftIndex(int leftIndex) {
		this.leftIndex = leftIndex;
	}

	@Override
	public String toString() {
		return "[EOP=" + leftIndex + "," + rightIndex
				+ "," + currentIndex + "," + TopIndex
				+ "]";
	}
	
	
}
