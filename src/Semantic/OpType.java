package Semantic;

import Debug.Debugger;

public class OpType {
	public Operator op;
	
	public OpType(OpType _op) {
		this.op = _op.op;
	}

	public OpType() {
	}

	public void setOp(String s){
		if(s.equalsIgnoreCase("add")){
			op = op.PlusOp;
		}
		else if( s.equalsIgnoreCase("sub")) {
			op = op.MinusOp;
		}
		else {
			Debugger.ERRORNOTHROW("inside setip", Debugger.ENDPROGRAM);
		}
	}

	@Override
	public String toString() {
		return "OpType[op=" + op + "]";
	}
	
}
