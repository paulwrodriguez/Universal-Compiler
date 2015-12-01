package Semantic;

public class ExprType {
	public static final boolean Temp = true;
	public ExprKind Kind;
	public String Name;
	public int val;
	
	public ExprType(boolean isTemp) {
		if(isTemp == Temp){
			Kind = ExprKind.TempExpr;
		}
	}
	public ExprType() {
	}
	public ExprType(ExprType right) {
		this.Kind = right.Kind;
		this.Name = right.Name;
		this.val = right.val;
	}
	@Override
	public String toString() {
		return "ExprType [Kind=" + Kind + ", Name=" + Name + ", val=" + val
				+ "]";
	}
	
	
}
