/*
 * Author: Paul Rodriguez
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Data.EOP;
import Data.ErrorException;
import Data.Grammar;
import Data.Production;
import Data.Symbol;
import Data.TermSet;
import Data.Token;
import Data.TokenCode;
import Data.TokenText;
import Debug.Debugger;
import Interfaces.ParseStackable;
import Interfaces.ParseStackable.StackableDataType;
import Interfaces.SemanticStackable;
import Semantic.Semantic;
import Tables.LLTableGen;


public class Parser {
	private Grammar grammar;
	private LLTableGen llTable;
	private ArrayList<TermSet> predictSet;
	//for lldriver
//	private LinkedList<Symbol> stack;
	// for llcompiler
	private LinkedList<ParseStackable> parseStack;
	private LinkedList<SemanticStackable> semanticStack;
	private LinkedList<Symbol> remInput;
	private Scanner sc;
	private TokenCode tc = new TokenCode();
	private TokenText tt = new TokenText();
	private String tokenSymbol;
	private int LeftIndex, RightIndex, CurrentIndex, TopIndex;
	private PrintWriter pw;
	private PrintWriter complerStatusOutputWriter;
	
	private static final boolean PRINT1 = false; // for outputting action, current state, parse stack
//	private static final boolean PRINT2 = true; // for pretty print function. not sure what it does
	private static final boolean OUTPUT_STATUS_TO_OUTPUT_WRITER = true;
	private static final boolean OUTPUT_REMAINING_INPUT = true; // for outputting remaining input, current state of parse and semantic stacks and generated code
	private static final boolean OUTPUT_PARSESTACK = true;
	private static final boolean OUTPUT_SEMANTICSTACK = true;
	private static final boolean OUTPUT_EOP = true;
	private static final boolean OUTPUT_GENERATED_CODE = true;
	private static final boolean OUTPUT_SYMBOLTABLE = true;
	private static final boolean OUTPUT_STRINGSPACE = true;
	private static final int PARSESTACK = 2;
	private static final int SEMANTICSTACK = 3;
	private static final int BOTH = 1;
	
	
	
	
	Parser(GrammarAnalyzer ga, Scanner sc, String _compilerStatusFileName, String _codeGenerationOutputFileName) throws FileNotFoundException{
		complerStatusOutputWriter = new PrintWriter(_compilerStatusFileName);
		Semantic.addPrintWriterForStatusOutput(complerStatusOutputWriter);
		Semantic.addCodeGenerationOutputFileName(_codeGenerationOutputFileName);
		grammar = ga.getGrammer();
		llTable = new LLTableGen();
		predictSet = ga.getPredictSet();
		try {
			generateLLTable();
		} catch (ErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Debugger.ERRORNOTHROW("generateLLTable failed to run", Debugger.ENDPROGRAM);
		}
		this.sc = sc;
//		stack = new LinkedList<Symbol>();
		parseStack = new LinkedList<ParseStackable>();
		semanticStack = new LinkedList<SemanticStackable>();
	}
	
	
	public LLTableGen generateLLTable() throws ErrorException{
		if(!llTable.isGenerated()){
			llTable = new LLTableGen(grammar.getAllLHS(), predictSet, grammar.getTerminals());
		}
		llTable.generate();
		return llTable;
	}
	
	// Parse driver
//	private void PrettyPrintStatus(String pa, LinkedList<Symbol> ri, String ps){
//		String t = "";
//		for(Symbol s : ri){
//			t =  t + s.getText();
//		if(PRINT1){
//			System.out.format("%1$-15s %2$-20s %3$-20s\n", pa, t, ps );
//		}
//		if(PRINT2){
//			System.out.print("parseStack : ");
//			parseStack.forEach(x -> System.out.print(" " + x.toString() +  " ") ); 
//			System.out.println();
//			System.out.print("semanticStack : ");
//			semanticStack.forEach(x -> System.out.print(" " + x.toString() +  " ") ); 
//			System.out.println();
//			
//		}
//	}
	private void Push(EOP record){
		if(Debugger.LLCOMPILER){
			System.out.println("pushing " + record.toString() + " onto parseStack");
		}
		parseStack.push(record);
	}
	public void LLCompiler() throws Exception {
//		int LeftIndex, RightIndex, CurrentIndex, TopIndex;
		
		// ******************************************************************//
		ParseStackable X = null;
		String a = null;
//		LinkedList<Symbol> remInput = new LinkedList<Symbol>();
		remInput = new LinkedList<Symbol>();
		String temp = "";
		// setup remaining input stack for output
		while(temp != Scanner.EOFS){
			temp = nextToken();
			remInput.push(new Symbol(false, true, true, false, tt.value));
		}
		Collections.reverse(remInput);
		sc.resetScanner();
		outputStructureStatus();
		// TODO put above in function
		// step 1
		Push(grammar.getStarting_symbol(), BOTH);
		
//		for(Symbol s : parseStack){
//			ParseStack += s.getText();
//		}
		// ******************************************************************//
		LeftIndex = 0;
		RightIndex = 0;
		CurrentIndex = 1;
		TopIndex = 2;
		
		a = nextToken();
		while(!ParseStackEmpty()){
			outputStructureStatus();
			X = TOP();
			if(X.is(StackableDataType.NONTERMINAL)){
				if(T(X, a) > 0){
					Pop();
					Push(new EOP(LeftIndex, RightIndex, CurrentIndex, TopIndex));
					// push ym -> y1 on parse stack
					Production p = grammar.getProductionByNumber(T(X,a));
					pushProductionToParseStack(p);
					int m = pushProductionToSemanticStack(p);
					LeftIndex = CurrentIndex;
					RightIndex = TopIndex;
					CurrentIndex = RightIndex;
					TopIndex = TopIndex + m;
				}
				else {
					Debugger.ERRORNOTHROW("llcompiler failed T(" +X.toString() + "," + a + ")", Debugger.ENDPROGRAM);
				}
			}
			else if(X.is(StackableDataType.TERMINAL)){
					if(tokenEquals(X, a)){
						insertSemanticRecord(a, CurrentIndex);
						Pop();
						if(OUTPUT_REMAINING_INPUT){
							remInput.pop();
						}
						a = nextToken();
						CurrentIndex++;
					}
					else {
						Debugger.ERRORNOTHROW(X.toString() + " != " + a, Debugger.ENDPROGRAM);
					}
				}
			else if(X.is(StackableDataType.EOP)) {
				LeftIndex = EOP.extractLeftIndex(X);
				RightIndex = EOP.extractRightIndex(X);
				CurrentIndex = EOP.extractCurrentIndex(X);
				TopIndex = EOP.extractTopIndex(X);
				CurrentIndex++;
				restoreTopOfSemanticStack(TopIndex);
				Pop();
			}
			else {
				if(X.is(StackableDataType.ACTIONSYMBOL)){
					Pop();
					Semantic.callRoutine(X, semanticStack, LeftIndex, RightIndex, CurrentIndex, TopIndex);
				}
				else {
					Debugger.ERRORNOTHROW("last else in llcompiler. X = " + X.toString(), Debugger.ENDPROGRAM);
				}
			}
		}
		if(OUTPUT_STATUS_TO_OUTPUT_WRITER){
			complerStatusOutputWriter.println("\n*****************************************************\n"); 
			complerStatusOutputWriter.flush();
		}

		
	}
	private void outputStructureStatus() {
		if(OUTPUT_STATUS_TO_OUTPUT_WRITER){
			complerStatusOutputWriter.println("\n*****************************************************\n"); 
			complerStatusOutputWriter.println("\n*****************************************************"); 
			if(OUTPUT_REMAINING_INPUT){
				complerStatusOutputWriter.format("%2$-15s (%-3d) : ", remInput.size(), "Remaining Input");
				remInput.forEach(x -> complerStatusOutputWriter.print(x.getText() + " "));
				complerStatusOutputWriter.println();
			}
			if(OUTPUT_PARSESTACK){
				complerStatusOutputWriter.format("%2$-15s (%-3d) : ", parseStack.size(), "Parse Stack" );
				parseStack.forEach(x -> complerStatusOutputWriter.print(x.toString() + " "));
				complerStatusOutputWriter.println();
			}
			if(OUTPUT_SEMANTICSTACK){
				complerStatusOutputWriter.format("%2$-15s (%-3d) : ", semanticStack.size(), "Semantic Stack");
				semanticStack.forEach(x -> complerStatusOutputWriter.print(x.toString() + " "));
				complerStatusOutputWriter.println();
			}
			if(OUTPUT_EOP){
				String o = "[" + LeftIndex + ',' + RightIndex + ',' + CurrentIndex + ',' + TopIndex + ']';
				complerStatusOutputWriter.format("%1$-22s: %2$s \n", "EOP",  o);
			}
			if(OUTPUT_SYMBOLTABLE){
				if(Semantic.hasStarted)
					complerStatusOutputWriter.format("%1$-21s : [Id|Scope_Num]\n%2$s", "Symbol Table", Semantic.getSymbolTable().dump());
				else{
					complerStatusOutputWriter.format("%1$-21s :\n", "Symbol Table");
				}
				
			}
			if(OUTPUT_STRINGSPACE){
				if(Semantic.hasStarted){
					complerStatusOutputWriter.format("%1$-21s : %2$s\n", "StringSpace", Semantic.getSymbolTable().getStringSpace().dump());
				}
			}
			if(OUTPUT_GENERATED_CODE){
				complerStatusOutputWriter.format("%-21s : ", "Generated Code");
			}
		}
	}


	private void restoreTopOfSemanticStack(int topIndex){
		while(semanticStack.size() >= topIndex){
			if(Debugger.LLCOMPILER){
				System.out.println("removing " + semanticStack.getFirst() + " from ss");
			}
			semanticStack.removeFirst();
		}
	}
	private void insertSemanticRecord(String a, int currentIndex){
		Token t = new Token(tt.value, tc.code, tokenSymbol);
		// reverse index because semantic static is opposite from parse stack which is keeping the index
		int reverseIndex = semanticStack.size() - currentIndex;
		if(Debugger.LLCOMPILER){
			System.out.println("replaceing " + semanticStack.get(reverseIndex) +" with " + t.toString() + " inside semanticstack");
		}
		semanticStack.set(reverseIndex, t);
	}
	private boolean tokenEquals(ParseStackable x, String a){
		Symbol X = (Symbol) x;
		if(X.getText().equalsIgnoreCase(a)){
			return true;
		}
		return false;
	}
	// Post: push rhs of production to semantic stack in forwards order. no including action symbols
	private int pushProductionToSemanticStack(Production p) throws ErrorException{
		int count = 0;
		Production pNoSymbols = new Production(p.getAllNoActionSymbols());
		for(int i = 0; i < pNoSymbols.getRHSLengthNoActionSymbols(); ++i){
			count += Push(pNoSymbols.getRHSSymbolNoAction(i), SEMANTICSTACK);
		}
		return count;
	}
	// Post: push rhs of production to parse stack in reverse order. all symbols
	private void pushProductionToParseStack(Production p) throws ErrorException{
		for(int i = p.getRHSLength()-1; i >= 0; --i){
			Push(p.getRHSSymbol(i), PARSESTACK);
		}
	}
	private boolean ParseStackEmpty() {
		return parseStack.isEmpty();
	}
	// refer to homework 8 for working copy of this function 
	/*
	public void LLDriver() throws Exception {
		Symbol X = null;
		String a = null;
		LinkedList<Symbol> remInput = new LinkedList<Symbol>();
		String temp = "";
		// setup remaining input stack for output
		while(temp != Scanner.EOFS){
			temp = nextToken();
			remInput.push(new Symbol(false, true, true, false, tt.value));
		}
//		remInput.push(new Symbol(false, true, true, false, "$"));
		Collections.reverse(remInput);
		sc.resetScanner();
		// TODO put above in function
		
		Push(grammar.getStarting_symbol());
		String ParseStack = "";
		String ParseAction = "";
		
		for(Symbol s : stack){
			ParseStack += s.getText();
		}
		
		a = nextToken();
		while(!StackEmpty()){
			X = TOP();
			if(X.isNonTerminal()){
				if(T(X,a) > 0){
					Pop();
					Production p = grammar.getProductionByNumber(T(X,a));
					if(Debugger.PARSER)
						System.out.println("pushing " + p.toString());
					for(int i = p.getRHSLengthNoActionSymbols()-1; i >= 0; --i){
						Push(p.getRHSSymbol(i));
					}
					ParseAction = "Predict " + T(X,a);
					ParseStack = "";
					for(Symbol s : stack){
						ParseStack += s.getText();
					}
					PrettyPrintStatus(ParseAction, remInput, ParseStack);
				}
				else{
					Debugger.ERROR("lldriver failed t(X,a)", Debugger.TERMINATE);
				}
			}
			else { // X is a terminal
				if(X.getText().equalsIgnoreCase(a)){
					Pop(); // match of X worked
					ParseAction = "Match " + a;
					ParseStack = "";
					for(Symbol s : stack){
						ParseStack += s.getText();
					}
					remInput.pop();
					PrettyPrintStatus(ParseAction, remInput, ParseStack);
					a = nextToken();
					if(a.equalsIgnoreCase(Scanner.EOFS)){
						Pop();
						remInput.pop();
						ParseAction = "Match " + a;
						ParseStack = "";
						for(Symbol s : stack){
							ParseStack += s.getText();
						}
						PrettyPrintStatus(ParseAction, remInput, ParseStack);
					}
				}
				else{
					Debugger.ERROR("failed match : X != a", Debugger.TERMINATE);
				}
			}
		}
		/*
		 * Push(S); — — Push the Start Symbol onto an empty stack
			while not StackEmpty
			loop
			— — let X be the top stack symbol; let a be the current input token
			if X in nonterminals
				then
				if T(X, a) = X —> Y1Y2. . .Ym
					then
					— — Expand nonterminal, replace X with Y1Y2. . .Ym on the stack.
					— — Begin with Ym, then Ym-1, . . . , and Y1 will be on top of the stack.
					else
					— — process syntax error
				end if;
			else — — X in terminals
				if X = a
					then
					Pop(1); — — Match of X worked
					Scanner(a); — — Get next token
					else
					— — process syntax error
					end if;
				end if;
			end loop;
		 */
		
	/*
	 * Helper Functions
	 */
	private void Pop(){
		if(Debugger.PARSER){
			System.out.println("Popping -> " + parseStack.getFirst() + " from parsestack");
		}
		parseStack.pop();
	}
	// TODO maybe remove this
//	private int T(Symbol X, String a){
//		
//		int result = 0;
//		result = llTable.getEntry(X, new Symbol(false, true, true, false, a));
//		if(Debugger.PARSER){
//			System.out.println("t(" + X.getText() + "," + a + ")=" + result);
//		}
//		return result;
//	}
	private int T(ParseStackable x, String a){
		// TODO remove this
		if(!(x instanceof Symbol)){
			Debugger.ERRORNOTHROW("error in T. not instance of symbol", Debugger.ENDPROGRAM);
		}
		Symbol X = (Symbol)x; 
		int result = 0;
		result = llTable.getEntry(X, new Symbol(false, true, true, false, a));
		if(Debugger.PARSER){
			System.out.println("t(" + X.getText() + "," + a + ")=" + result);
		}
		return result;
	}
	private String nextToken() throws Exception{
		tc = new TokenCode();
		tt = new TokenText();
		String token = "";
		
		token = sc.Scan(tc, tt);
		tokenSymbol = token;
		if(token == Scanner.EOFS) {
			return Scanner.EOFS;
		}
		if(token.equalsIgnoreCase("id") || token.equalsIgnoreCase("intliteral")){
			return token;
		}
		return tt.value;
	}
	// Post: symbol has been pushed unto the parsestack
	private int Push(Symbol s, int which){
		int count = 0;
		if(!s.equals(Symbol.LAMPDA)){
			
			if(which == PARSESTACK){
				parseStack.push(s);
				if(Debugger.LLCOMPILER){
					System.out.println("pushing to parsestack -> {  " + s.getText() + "}");
				}
			}
			else if(which == SEMANTICSTACK){
				semanticStack.push(s);
				if(Debugger.LLCOMPILER){
					System.out.println("pushing to semanticStack -> {  " + s.getText() + "}");
				}
			}
			else if(which == BOTH){
				parseStack.push(s);
				semanticStack.push(s);
				if(Debugger.LLCOMPILER){
					System.out.println("pushing to parse & semantic stack -> {  " + s.getText() + "}");
				}
			}
			else {
				Debugger.ERRORNOTHROW("push(which != known type", Debugger.ENDPROGRAM);
			}
			++count;
			
		}
		return count;
	}
	
//	private boolean StackEmpty(){
//		return stack.isEmpty();
//	}
	
	private ParseStackable TOP(){
		return parseStack.getFirst();
	}
	/*
	 * Helper Functions
	 */


	public LLTableGen getLLTable() {
		return llTable;
	}
}
