package Semantic;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import Data.ErrorException;
import Data.Symbol;
import Debug.Debugger;
import Interfaces.ParseStackable;
import Interfaces.SemanticStackable;
import SymbolTable.SymbolTable;

public class Semantic {

	private static int MaxSymbol;
	private static int MaxTemp;
	private static int LastSymbol;
	private static PrintWriter pw;
	private static PrintWriter complerStatusOutputWriter;
	private static String outFileForCodeGeneration;
//	private static ArrayList<String> symbolTable;
	private static SymbolTable st;
	private static LinkedList<SemanticStackable> ss;
	private static int LeftIndex;
	private static int RightIndex;
	private static int CurrentIndex;
	private static int TopIndex;
	private static int tempCounter;
	private static final boolean OUTPUT_CODE_GENERATION = true;
	private static int num_of_output_per_semantic_routine;
	
	public static boolean hasStarted = false;

	private enum Routines {
		Start, Finish, ProcessId, Copy, ProcessOp, ProcessLiteral, GenInfix, Assign, ReadId, WriteExpr, StartBlock, EndBlock,
		ProcessVar, DeclareInt;
		public static ArrayList<Routines> getNames() {
			ArrayList<Routines> all = new ArrayList<Routines>(EnumSet.allOf(Routines.class));
			return all;
		}
	};

	private Semantic() {
	}

	static void Assign(SemanticRecord Target, SemanticRecord Source) {
		Generate("Store", Extract(Source), Target.expreRecord.Name);
	}

	static void ReadId(SemanticRecord InVar) {
		if(Debugger.SEMANTIC){
			System.out.println("Read " + InVar);
		}
		Generate("Read", InVar.expreRecord.Name, "Integer");
	}

	static void WriteExpr(SemanticRecord OutExpr) {
		if(Debugger.SEMANTIC){
			System.out.println("Write " + OutExpr);
		}
		Generate("Write", Extract(OutExpr), "Integer");
	}

	public static void callRoutine(ParseStackable x,
			LinkedList<SemanticStackable> semanticStack, int leftIndex,
			int rightIndex, int currentIndex, int topIndex)
			throws ErrorException {
		if(OUTPUT_CODE_GENERATION){
			num_of_output_per_semantic_routine = 0;
		}
		ss = semanticStack;
		int offSet = ss.size();
		// ss is backwards. normalize class indexes for ease
		LeftIndex = offSet - leftIndex;
		RightIndex = offSet - rightIndex;
		CurrentIndex = offSet - currentIndex;
		TopIndex = offSet - topIndex;

		Symbol s = (Symbol) x;
		SemanticStackable st = ss.get(semanticStack.size() - leftIndex);
		// SemanticRecord E;
		SemanticRecord param1;
		SemanticRecord param2;
		SemanticRecord param3;
		SemanticRecord result;
		switch (extractRoutine(s.getText())) {
		case Start:
			Start();
			break;
		case Finish:
			Finish();
			break;
		case Copy:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			param2 = new SemanticRecord(ss.get(extractParam(s.getText(), 2)));
			Copy(param1, param2);
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			insertSemanticRecordAt(param2, extractParam(s.getText(), 2));
			break;
		case ProcessId:
			param1 = new SemanticRecord(ss.get(LeftIndex));
			insertSemanticRecordAt(param1, LeftIndex);
			ProcessId(param1);
			break;
		case ProcessOp:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			ProcessOp(param1);
			break;
		case ProcessLiteral:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			ProcessLiteral(param1);
			break;
		case GenInfix:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			param2 = new SemanticRecord(ss.get(extractParam(s.getText(), 2)));
			param3 = new SemanticRecord(ss.get(extractParam(s.getText(), 3)));
			result = new SemanticRecord(ss.get(extractParam(s.getText(), 4)));
			GenInfix(param1, param2, param3, result);
			insertSemanticRecordAt(result, extractParam(s.getText(), 4)); // save result

			break;
		case Assign:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			param2 = new SemanticRecord(ss.get(extractParam(s.getText(), 2)));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			Assign(param1, param2);
			break;
		case ReadId:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			ReadId(param1);
			break;
		case WriteExpr:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			WriteExpr(param1);
			break;
		case ProcessVar:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			ProcessVar(param1);
			break;
		case DeclareInt:
			param1 = new SemanticRecord(ss.get(extractParam(s.getText(), 1)));
			insertSemanticRecordAt(param1, extractParam(s.getText(), 1));
			DeclareInt(param1);
			break;
		case StartBlock:
			StartBlock();
			break;
		case EndBlock:
			EndBlock();
			break;
		default:
			System.out.println("No routine defined for " + s.getText());
		}
	}

	private static void GenInfix(SemanticRecord E1, SemanticRecord Op,
			SemanticRecord E2, SemanticRecord Result) {
		if(Debugger.SEMANTIC){
			System.out.println("GenInfix before");
			System.out.println("E1 = " + E1); 
			System.out.println("Op = " + Op); 
			System.out.println("E2 = " + E2); 
			System.out.println("Re = " + Result); 
		}
		SemanticRecord ERec = new SemanticRecord(SemanticRecord.TempExpr);
		ERec.expreRecord.Name = GetTemp();
		Generate(Extract(Op), Extract(E1), Extract(E2), ERec.expreRecord.Name);
		if (Debugger.SEMANTIC) {
			System.out.print("Geninfix result = ERec " + Result + " = " + ERec
					+ " -> ");
		}
		// save ERec as the result
		Result.expreRecord.Kind =  ERec.expreRecord.Kind;
		Result.expreRecord.Name =  ERec.expreRecord.Name;
		Result.expreRecord.val	=  ERec.expreRecord.val;
		
		
		if(Debugger.SEMANTIC){
			System.out.println("GenInfix after");
			System.out.println("E1 = " + E1); 
			System.out.println("Op = " + Op); 
			System.out.println("E2 = " + E2); 
			System.out.println("Re = " + Result); 
		}
	}

	private static String GetTemp() {
		String result = "Temp&" + tempCounter++;
		Generate("Declare", result, "Integer");
		if (Debugger.SEMANTIC) {
			System.out.println("GetTemp " + result);
		}
		return result;
	}

	private static void ProcessLiteral(SemanticRecord E) {
		if (Debugger.SEMANTIC) {
			System.out.println("ProcessLiteral before" + E);
		}
		E.recordKind = SemanticRecordKind.ExprRec;
		E.expreRecord.Kind = ExprKind.LiteralExpr;
		E.expreRecord.val = Integer.parseInt(Extract(SemanticRecordConverter(ss
				.get(CurrentIndex + 1))));
		if (Debugger.SEMANTIC) {
			System.out.println("ProcessLiteral after" + E);
		}

	}

	private static void ProcessOp(SemanticRecord E) {
		if (Debugger.SEMANTIC) {
			System.out.println("ProccessOp before" + E);
		}
		E.recordKind = SemanticRecordKind.OpRec;
		E.opRecord.setOp(Extract(SemanticRecordConverter(ss
				.get(CurrentIndex + 1))));
		if (Debugger.SEMANTIC) {
			System.out.println("ProccessOp after " + E);
		}
	}

	private static void Copy(SemanticRecord source, SemanticRecord dest) {
		if (Debugger.SEMANTIC) {
			System.out.println("Copying " + source + " to " + dest);
		}
		dest.deepCopy(source); 
	}

	private static int extractParam(String text, int paramNumber) {
		String[] array = text.split(",");
		if (paramNumber < array.length) {
			int startIndex = array[paramNumber - 1].indexOf("$") + 1;
			String param = array[paramNumber - 1].substring(startIndex,
					array[paramNumber - 1].length());
			if (param.equalsIgnoreCase("$")) {
				return LeftIndex;
			} else {
				int value = Integer.parseInt(param);
				return RightIndex - value + 1;
			}

		} else if (paramNumber == array.length) {
			int startIndex = array[paramNumber - 1].indexOf("$") + 1;
			String param = array[paramNumber - 1].substring(startIndex,
					array[paramNumber - 1].length() - 1);
			if (param.equalsIgnoreCase("$")) {
				return LeftIndex;
			} else {
				int value = Integer.parseInt(param);
				return RightIndex - value + 1;
			}
		} else {
			Debugger.ERRORNOTHROW("could not parse " + text,
					Debugger.ENDPROGRAM);
		}
		// handle final index
		return -1;
	}

	private static void insertSemanticRecordAt(SemanticRecord param1, int index) {
		if (Debugger.SEMANTIC) {
			System.out.println("swaping " + param1 + " for " + ss.get(index));
		}
		ss.set(index, param1);
	}

	private static SemanticRecord ProcessVar(SemanticRecord e) throws ErrorException {
		e.recordKind = SemanticRecordKind.ExprRec;
		e.expreRecord.Kind = ExprKind.IdExpr;
		e.expreRecord.Name = Extract(SemanticRecordConverter(ss.get(CurrentIndex + 1))); // taking information from Id listed in grammar
		return e;
	}
	private static SemanticRecord DeclareInt(SemanticRecord e) throws ErrorException{
		if(!LookUp(Extract(e))) {
			Generate("Declare", Extract(e), "Integer");
		}
		EnterIntoSymbolTable(Extract(e));
//		CheckId(Extract(e));
		return e;
	}
	private static SemanticRecord ProcessId(SemanticRecord e)
			throws ErrorException {
		/*
		 * procedure ProcessId(E: out SemanticRecord) is begin
		 * CheckId(Extract(SS[CurrentIndex – 1])); E.RecordKind := ExprRec
		 * E.ExprRecord.Kind := IdExpr, E.ExprRecord.Name :=
		 * Extract(SS[CurrentIndex – 1]); end ProcessId
		 */
		if (Debugger.SEMANTIC) {
			System.out.println("ProcessId before " + e);
		}
		e.recordKind = SemanticRecordKind.ExprRec;
		e.expreRecord.Kind = ExprKind.IdExpr;
		e.expreRecord.Name = Extract(SemanticRecordConverter(ss.get(CurrentIndex + 1)));
		// CheckId(Extract(e));
		if(!LookUp(Extract(e))){
			Debugger.ERRORNOTHROW("Semantic Error. " + Extract(e) + " has not been declared yet.", Debugger.ENDPROGRAM);
		}
		if (Debugger.SEMANTIC) {
			System.out.println("ProcessId after  " + e);
		}
		return e;
	}

	private static SemanticRecord SemanticRecordConverter(
			SemanticStackable toConvert) {
		SemanticRecord result = new SemanticRecord(toConvert);
		return result;
	}

	/*
	 * Auxilary Functions
	 */
	private static String Extract(SemanticRecord E) {
		switch (E.recordKind) {
		case OpRec:
			return ExtractOp(E);
		case ExprRec:
			return ExtractRect(E);
		case Error:
			return null;
		}

		return null;
	}

	private static String ExtractRect(SemanticRecord e) {
		return e.ExtractRec();
	}

	private static String ExtractOp(SemanticRecord e) {
		return e.ExtractOp();
	}

	// Post: If variable s is not decalred yet CheckId enters it into the symbol
	// table and then generates
	// the assembler directive to reserve space for it
	private static void CheckId(String s) throws ErrorException {
		// TODO proposed change checkid to simply check if id is already declared. 
		if (!LookUp(s)) {
			EnterIntoSymbolTable(s);
			Generate("Declare", s, "Integer");
		}
	}

	private static void EnterIntoSymbolTable(String s) throws ErrorException {
		st.enter(s);
	}

	private static boolean LookUp(String s) {
		boolean found = st.find(s);
		return found;
	}

	/*
	 * End of Auxilary Functions
	 */
	private static void Start() {
		if (Debugger.SEMANTIC) {
			System.out.println("Calling Start Routine");
		}
		MaxSymbol = 1000;
		MaxTemp = 0;
//		symbolTable = new ArrayList<String>();
		st = new SymbolTable();
		LastSymbol = 0;
		try {
			pw = new PrintWriter(outFileForCodeGeneration);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Debugger.ERRORNOTHROW("could not create " + outFileForCodeGeneration,
					Debugger.ENDPROGRAM);
		}
		hasStarted = true;
	}

	private static void Finish() {
		if (Debugger.SEMANTIC) {
			System.out.println("Calling Finish Routine");
		}
		Generate("Halt");
	}

	private static void StartBlock() {
		st.incrementScope();
	}
	private static void EndBlock() {
		st.removeCurrentScope();
		st.decrementScope();
	}
	private static void Generate(String... cmd) {
		String result = "";
		for (int i = 0; i < cmd.length; ++i) {
			if (i != 0)
				result += ',';
			result += cmd[i];
		}
		// TODO remove this
		if (result.equals("")) {
			System.out.println("output empty string");
			System.exit(0);
		}
		Write(result);
	}

	// Post: Appends \n at the end of every right
	private static void Write(String output) {
		pw.println(output);
		pw.flush();
		if(OUTPUT_CODE_GENERATION) {
			
			if(num_of_output_per_semantic_routine++ == 0){
				complerStatusOutputWriter.print(output);
			}
			else {
				complerStatusOutputWriter.format("\n%1$-23s %2$s", "", output);
			}
		}
	}

	private static String extractActionSymbolText(String text){
		int start = text.indexOf("#");
		int end = (text.indexOf("(") == -1) ? text.length() : text.indexOf("(");
		
		return text.substring(start + 1, end).trim();
	}
	private static Routines extractRoutine(String text) {
		
		String actionSymbolText = extractActionSymbolText(text);
		for(Routines r : Routines.getNames()){
			if(actionSymbolText.equalsIgnoreCase(r.name())){
				return r;
			}
		}
		
		// Program Should not reach this point. 
		Debugger.ERRORNOTHROW("Routine " + text + " not defined. Exiting Program.", Debugger.ENDPROGRAM);
		return null;
	}

	public static void addPrintWriterForStatusOutput(PrintWriter _pw) {
		complerStatusOutputWriter = _pw;
		
	}

	public static void addCodeGenerationOutputFileName(
			String _codeGenerationOutputFileName) {
		outFileForCodeGeneration = _codeGenerationOutputFileName;
	}

	public static SymbolTable getSymbolTable() {
		return st;
	}
}
