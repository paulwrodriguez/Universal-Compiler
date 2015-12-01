import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import Data.Symbol;
import Data.TermSet;
import Data.TokenCode;
import Data.TokenText;
import Debug.Debugger;
import SymbolTable.StringSpace;
import SymbolTable.SymbolTable;

public class Homework {

	class a{
		public int a;
	};
	
	class b extends a{
		public b() {
		}
		public int b;
	};
	public static void main(String[] args) throws Exception {
		System.out.println("*Start of program*\n");
		hw9();
//		test();
		System.out.println("\n*End  of prSogram*");
	}	
	
	private static void test() {
		SymbolTable st = new SymbolTable();
		st.test();
		return;
		
		
	}

	private static void hw9() throws Exception {
		String grammarFileName = "hw10input.txt";
		String codeGenerationOutputFileName = "hw9output.txt";
		String codeToBeRead = "hw8input.txt";
		String compilerStatusFileName = "hw9programstatus.txt";
		
		System.out.println("Reading " + grammarFileName + ". writing to "+ codeGenerationOutputFileName + " & " + compilerStatusFileName );
		
		GrammarAnalyzer ga = new GrammarAnalyzer(grammarFileName);
		ga.analyze();
	
		Scanner sc = new Scanner(codeToBeRead);
		Parser p = new Parser(ga, sc, compilerStatusFileName, codeGenerationOutputFileName);
		p.LLCompiler(); // for testing output
	}

	@Deprecated
	private static void hw8() throws Exception{
		String grammarFileName = "hw7input.txt";
		String outFileName = "hw8output.txt";
		String codeToBeRead = "hw8input.txt";
		
		System.out.println("Start of Program");
		System.out.format("%1$-15s %2$-20s %3$-20s\n", "ParserAction", "Remaining Input", "Parse Stack" );
		GrammarAnalyzer ga = new GrammarAnalyzer(grammarFileName);
		PrintWriter pw = new PrintWriter(outFileName);
		ga.analyze();
	
		Scanner sc = new Scanner(codeToBeRead);
//		Parser p = new Parser(ga, sc, );
		//p.LLDriver(); // for testing output
		
		System.out.println("End of Program");
		
	}
	@Deprecated
	private static void hw7() throws Exception {
		String firstGrammer = "hw7input.txt";
		String outFile = "hw7output.txt";
		PrintWriter pw = new PrintWriter(outFile);
		System.out.println("First Grammar Analyzed: " + firstGrammer);
		GrammarAnalyzer ga = new GrammarAnalyzer(firstGrammer);
		ga.analyze();
		System.out.println("Writing anaylzed grammar to file: " + outFile + "..." );
		pw.println("**Start of : " + outFile + " grammar**" );
//		Parser p = new Parser(ga, sc, );
//		pw.println(p.getLLTable().toString());
		pw.close();
	}
	@Deprecated
	private static void mapTest() {
		HashMap<Symbol, TermSet> firstSet = new HashMap<Symbol, TermSet>();
		HashMap<Symbol, TermSet> second = new HashMap<Symbol, TermSet>();
		Symbol sym = new Symbol(false, false, false, false, "one");
		Symbol sym2 = new Symbol(false, false, false, false, "two");
		Symbol sym3 = new Symbol(false, false, false, false, "three");
		firstSet.put(Symbol.LAMPDA, new TermSet(sym));
		second.put(sym, new TermSet());
		second.put(sym, second.get(sym).add(sym2));
		second.get(sym).add(second.get(sym).setSubstract(sym2));
		second.put(sym, second.get(sym).setSubstract(sym2));
//		second.get(sym).add(sym2);
		second.forEach((k,v) -> System.out.println("key: " + k + " -> value: " + v));
	}
	@Deprecated
	private static void hw6() throws Exception {
		String firstGrammer = "hw6input.txt";
		String outFile = "hw6output.txt";
		PrintWriter pw = new PrintWriter(outFile);
		System.out.println("Grammar Analyzed: " + firstGrammer);
		GrammarAnalyzer ga = new GrammarAnalyzer(firstGrammer);
		ga.analyze();
		System.out.println("Writing anaylzed grammar to file: " + outFile + "..." );
		pw.print("Predict Set: ");
		pw.println("<nonterminal> -> Y1, Y2, ... Yn");
		for(int i = 0; i < ga.getPredictSet().size(); ++i){
			pw.println("Predict(" + ga.getGrammer().getProduction(i) + ") " + " = " + ga.getPredictSet().get(i));
		}
		pw.close();
		if(Debugger.DEBUG){
			Set<Symbol> noDups = new HashSet<Symbol>();
			noDups.addAll(ga.getAllSymbols());
			for(Symbol s : noDups){
				System.out.println(s.toString()+"|");
			}
			System.out.println("---");
			for(Symbol v : ga.getGrammer().getVocabulary()){
				System.out.println(v.getText()+"|");
			}
		}
	}
	@Deprecated
	private static void hw5() throws Exception {
		// test split function
		String firstGrammer = "hw5input.txt";
		String secondGrammer = "hw5secondgrammer.txt";
		String outFile = "hw5output.txt";
		PrintWriter pw = new PrintWriter(outFile);
		System.out.println("First Grammar Analyzed: " + firstGrammer);
		GrammarAnalyzer ga = new GrammarAnalyzer(firstGrammer);
		ga.analyze();
		System.out.println("Writing anaylzed grammar to file: " + outFile + "..." );
		pw.println("**Start of : " + outFile + " grammar**" );
		pw.println(ga.getProductionAndSymbolDetails());

		
		System.out.println("Second Grammar Analyzed : " + secondGrammer);
		ga = new GrammarAnalyzer(secondGrammer);
		ga.analyze();
		System.out.println("Writing anaylzed grammar to file: " + outFile + "...");
		pw.println("**Start of : " + outFile + " grammar**");
		pw.println(ga.getProductionAndSymbolDetails());
		
		
		pw.close();
		return;
	}
	@Deprecated
	private static void printArray(Vector<String> a){
		for(String s : a){
			System.out.println(s);
		}
	}
	@Deprecated
	private static void hw4() throws Exception {
		Scanner sc = new Scanner("hw3input.txt");
		PrintWriter pw = new PrintWriter("hw4NewScanner1.txt");
		
		TokenCode code = new TokenCode();
		TokenText text = new TokenText();
		
		pw.format("%-10s %-10s\n", "TokenText", "TokenCode");
		while(code.code != 2) {
			pw.format("%-10s %-10d\n", sc.Scan(code, text), code.code);
		}
		
		sc.close();
		pw.close();
		
		sc = new Scanner("testData.txt");
		pw = new PrintWriter("hw4NewScanner2.txt");
		code = new TokenCode();
		text = new TokenText();
		
		pw.format("%-10s %-10s\n", "TokenText", "TokenCode");
		while(code.code != 2) {
			pw.format("%-10s %-10d\n", sc.Scan(code, text), code.code);
		}
		
		pw.close();
		sc.close();
	}
}
