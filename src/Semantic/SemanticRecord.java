/*
 * Requirements: convert needs to be updated everytime grammar with new record types are introduced. e.g. multiplication new oprec
 */

package Semantic;

import Data.Symbol;
import Data.Token;
import Debug.Debugger;
import Interfaces.SemanticStackable;

public class SemanticRecord implements SemanticStackable{
		
	public static final boolean TempExpr = true;
	public SemanticRecordKind recordKind;
	public OpType opRecord;
	public ExprType expreRecord;
	public StackableDataType mytype;
	public SemanticStackable record;
	
	public SemanticRecord(SemanticStackable _record) {
		if(!_record.is(SemanticStackable.StackableDataType.SEMANTICRECORD)) {
			record = _record; // avoid a linked list forming
			opRecord = new OpType();
			expreRecord = new ExprType();
			convert();
		}
		else {
			SemanticRecord _s = (SemanticRecord) _record;
			this.recordKind = _s.recordKind;
			this.opRecord = new OpType(_s.opRecord);
			this.expreRecord = new ExprType(_s.expreRecord);
			this.mytype = _s.mytype;
			this.record = _s.record;
		}
		mytype = StackableDataType.SEMANTICRECORD;
	}
	public SemanticRecord(SemanticRecord _s){
		this.recordKind = _s.recordKind;
		this.opRecord = new OpType(_s.opRecord);
		this.expreRecord = new ExprType(_s.expreRecord);
		this.mytype = _s.mytype;
		this.record = _s.record;
	}
	public SemanticRecord(boolean isTemp) {
		if(isTemp == TempExpr){
			record = null;
			expreRecord = new ExprType(ExprType.Temp);
			opRecord  = new OpType();
		}
	}

	private void convert() {
		if(record.is(StackableDataType.TOKEN)){
			Token t = (Token) record;
			if(t.getSymbol().endsWith("Op")){
				recordKind = SemanticRecordKind.OpRec;
			}
			else {
				recordKind = SemanticRecordKind.ExprRec;
			}
		}
	}
	
	public void setError() {
		recordKind = SemanticRecordKind.Error;
	}
	public String ExtractOp(){
		if(opRecord != null && opRecord.op != null){
			if (opRecord.op == Operator.PlusOp ) {
				return "Add ";
			} else if(opRecord.op == Operator.MinusOp) {
				return "Sub ";
			}
			else {
				Debugger.ERRORNOTHROW("Error semanticRecord.extractOp " + this.toString(), Debugger.ENDPROGRAM);
			}
		}
		Token t = (Token) record;
		if (t.getSymbol().equalsIgnoreCase("PlusOp")){
			opRecord.op = Operator.PlusOp;
			return "Add";
		}
		else if(t.getSymbol().equalsIgnoreCase("minusOp")) {
			opRecord.op = Operator.MinusOp;
			return "Sub";
		}
		else {
			setError();
			return null;
		}
	}
	public String ExtractRec() {
		if(recordKind == SemanticRecordKind.ExprRec && expreRecord.Kind != null){
			switch(expreRecord.Kind){
			case IdExpr:
			case TempExpr:
				return expreRecord.Name;
			case LiteralExpr:
				return expreRecord.val + "";
			}
		}
		// TODO change this so that tokens are taken from the start
		// TODO check if the below is even needed?
		Token t = (Token) record;
		if(t.getSymbol().equalsIgnoreCase("intliteral")) {
			expreRecord.Kind = ExprKind.LiteralExpr;
		}
		if(t.getSymbol().equalsIgnoreCase("id")) {
			expreRecord.Kind =  ExprKind.IdExpr;
		}else {
			expreRecord.Kind =  ExprKind.TempExpr; 
		}
		return t.getText();
	}

	@Override
	public String toString() {
		return record.toString();
//		return "SemanticRecord=[recordKind=" + recordKind + ",opRecord="
//				+ opRecord + ",expreRecord=" + expreRecord + ",mytype="
//				+ mytype + ",record=" + record + "]";
	}
	@Override
	public boolean is(Interfaces.ParseStackable.StackableDataType isThisType) {
		// TODO Auto-generated method stub
		return mytype == isThisType;
	}
	public void deepCopy(SemanticRecord source) {
		this.recordKind = source.recordKind;
		this.opRecord = new OpType(source.opRecord);
		this.expreRecord = new ExprType(source.expreRecord);
		this.mytype = source.mytype;
		// this.record = source.record; // for clarity do not copy the record e.g. copying <ident> to <primary> will remove <primary> and will make the log files hard to read. this may be required. 
		this.mytype = source.mytype;
	}

}
