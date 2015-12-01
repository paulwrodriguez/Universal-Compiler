/*
 * Author: Paul Rodriguez
 * Grammar Requirements:
 * 	1) Format: <non*terminal> -> [terminal]\s[terminal]<non*terminal> lampda
 * 	2) lampda character must be spelled out
 * 	3) any adjacent terminal symbols must be space separated
 * 	4) space no required before or after -> or < or >
 * 	5) starting_symbol=<> is first line of grammar file
 *  6) grammar should not end with $ unless that is an actual token that should be found in langauge
 */
/*
 * Symbol
 * 	Assumptions:
 * 		1) 2 symbols are the same if there text component is the same. regardless of LHS | RHS
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import Data.ErrorException;
import Data.Grammar;
import Data.MarkedVocabulary;
import Data.Production;
import Data.Symbol;
import Data.TermSet;
import Debug.Debugger;
import Tables.LLTableGen;

public class GrammarAnalyzer {
	public FileReader fr;
	public BufferedReader bf;
	public Grammar grammar;
	private MarkedVocabulary derivesLampda;
	private HashMap<Symbol, TermSet> firstSet;
	private HashMap<Symbol, TermSet> followSet;
	private ArrayList<TermSet> predictSet;

	public GrammarAnalyzer(String fileName) throws Data.ErrorException {
		try {
			fr = new FileReader(fileName);
			bf = new BufferedReader(fr);
			grammar = new Grammar();
			derivesLampda = new MarkedVocabulary();
			firstSet = new HashMap<Symbol, TermSet>();
			followSet = new HashMap<Symbol, TermSet>();
			predictSet = new ArrayList<TermSet>();

		} catch (IOException e) {
			e.printStackTrace();
			Debugger.ERROR("ERROR: GrammarAnalyzer could not open " + fileName
					+ "\nClosing Progriam", Debugger.TERMINATE);
		}
	}

	public Grammar getGrammer() {
		return grammar;
	}

	public ArrayList<Symbol> getAllSymbols() {
		return grammar.getAllSymbols();
	}

	private void readGrammarFromFile() throws IOException, ErrorException {
		// read starting symbol
		if (bf.ready()) {
			String starting_symbol = bf.readLine();
			starting_symbol.toLowerCase();
			if (starting_symbol.contains("starting_symbol=")) {
				grammar.setStarting_symbol(starting_symbol.split("=")[1].trim());
			} else {
				Debugger.ERROR(
						"could not read starting_symbol. syntax \'starting_symbol=<nonterminal>\' ",
						Debugger.TERMINATE);
			}
		} else {
			Debugger.ERROR("cannot read grammar file. buffer not ready",
					Debugger.TERMINATE);
		}
		while (bf.ready()) {
			/*
			 * when Letter or character or digit att to current token when < if
			 * current token length > 0 save current token clear current token
			 * add c to current token else add c to current token when ' ' if
			 * current token length > 0 save current token clear current token
			 * when > save current token clear current token
			 */
			if (Debugger.DEBUG) {
				System.out.println("--------------");
			}
			String[] lines = bf.readLine().split("->");
			if (lines.length != 2) {
				throw new Error(
						"ERROR: Grammar not in proper format. <nonterminal> -> <LHS>");
			}

			// save the LHS of the first terminal
			Production p = new Production();
			lines[0] = lines[0].trim();
			p.add(new Symbol(true, false, false, true, lines[0]));

			ArrayList<String> items = new ArrayList<String>();
			String current = "";
			boolean nonterminal = false;
			boolean isaction = false;
			// parse the RHS
			for (char c : lines[1].toCharArray()) {
				boolean hasChar = Character.toString(c).matches("[a-zA-Z]");
				if (hasChar) {
					current += c;
				} else if (c == '<') {
					if (current.length() > 0) {
						p.add(new Symbol(false, true, true, false, current));
						items.add(current);
						current = "";
					}
					nonterminal = true;
					current += c;
				} else if (c == ' ') {
					if (current.length() > 0 && !nonterminal) {
						if(isaction){
							p.add(new Symbol(Symbol.ACTIONSYMBOL, current));
							isaction = false;
						}
						else {
							p.add(new Symbol(false, true, true, false, current));
						}
						items.add(current);
						current = "";
					} else if (nonterminal) {
						current += c;
					}
				} else if (c == '>') {
					if (current.length() > 0) {
						current += c;
						p.add(new Symbol(false, true, false, true, current));
						items.add(current);
						current = "";
						nonterminal = false;
					} else {
						throw new Error(
								"ERROR reading grammar. Cant have > without  first <");
					}
				} else if(c == '#'){
					if(current.length() > 0 && !nonterminal){
						Debugger.ERRORNOTHROW("inside # and currentLength > 0 and terminal?", Debugger.ENDPROGRAM);
					}
					current +=c;
					isaction = true;
					
				} else if (hasSpecialCharacter(c)) {
					current += c;
				} else {
					throw new Error("ERROR with reading grammar at: " + c);
				}
			}
			if (current.length() > 0) {
				if(isaction){
					p.add(new Symbol(Symbol.ACTIONSYMBOL, current));

				}
				else if(nonterminal){
					p.add(new Symbol(Symbol.NONTERMINALSYMBOL_R, current));
				}
				else {
					p.add(new Symbol(Symbol.TERMINALSYMBOL, current));
				}
				items.add(current);
				current = "";
			}
			grammar.add(p);
			if (Debugger.DEBUG) {
				print(items);
				System.out.println(p.toString());
			}
		}
		if(Debugger.GRAMMAR){
			for(int i = 0; i < grammar.size(); ++i) {
				Debugger.outputController(grammar.getProductionAndSymbolDetails());
			}
		}
	}
	public void analyze() throws Exception {
		if (Debugger.DEBUG) {
			System.out.println("Start of grammar analyzer");
		}
		
		// read grammar
		readGrammarFromFile();
				
		
		// check for reserved words (i.e. lampda)
		checkExceptions();
		
		// get the vocabular from read grammar
		grammar.extractVocabularyFromGrammer();
		grammar.extractNonTerminals();
		grammar.extractTerminals();
		
		// fill sets
		MarkLambda();
		FillFirstSet();
		FillFollowSet();
		fillPredictSet();
		
	}

	
	private void fillPredictSet() throws Data.ErrorException {
		for (Production p : grammar.getProductions()) {
			predict(p);
		}
		if (Debugger.DEBUG) {
			System.out.println("predict set -> ");
			predictSet.forEach((k) -> System.out.println("predict set Production "
					+ k ));
		}
	}

	public ArrayList<TermSet> getPredictSet() {
		return predictSet;
	}

	private void predict(Production p) throws Data.ErrorException {
		/*
		 * Predict(A —> X1X2. . . Xm) = if each symbol in production has lampda
		 * then predict = first(p) U follow(A) - lampda else predict = first(p)
		 */
		boolean containLampda = true;
		for (Symbol s : p.getRHSNoAction()) {
			if (!firstSet.get(s).contains(Symbol.LAMPDA)) {
				containLampda = false;
				break;
			}
		}
		if (containLampda) {
			predictSet.add(first(new Production(p.getRHSNoAction() ) )
					.setAdd(followSet.get(p.getLHSSymbol()))
					.setSubstract(Symbol.LAMPDA) );
		} else {
			predictSet.add(first(new Production(p.getRHSNoAction())));
		}

	}

	private TermSet first(Production rhs) {
		/*
		 * return the first firstset that has something other then lampda and
		 * emptyset
		 */
		for (Symbol s : rhs.getAllNoActionSymbols()) {
			if (firstSet.get(s).setSubstract(Symbol.LAMPDA).setSubstract(Symbol.EMPTYSET).size() > 0) {
				return firstSet.get(s);
			}
		}
		return new TermSet(Symbol.LAMPDA);
	}
	
	private void FillFollowSet() throws Data.ErrorException, Exception {
		for (Symbol A : grammar.getNonTerminals()) {
			followSet.put(A, new TermSet(Symbol.EMPTYSET));
		}
		followSet.put(grammar.getStarting_symbol(), new TermSet(Symbol.LAMPDA));
		boolean changes = true;
		while (changes) {
			changes = false;
			for (Production p : grammar.getProductions()) {
				int length = p.getRHSLengthNoActionSymbols();
				for (int i = 0; i < length; ++i) {
					Symbol B = p.getRHSSymbolNoAction(i);
					ArrayList<Symbol> y = p.getRHSAfterNoActionSymbols(i);
					if (B.isNonTerminal()) {
						TermSet future = new TermSet(followSet.get(B));
						TermSet current = new TermSet(followSet.get(B));
						future.add(ComputerFirst(y).setSubstract(Symbol.LAMPDA));
						if(!current.containsAll(future)){
							changes = true;
						}
						followSet.get(B).add(ComputerFirst(y).setSubstract(Symbol.LAMPDA));
						if (ComputerFirst(y).contains(Symbol.LAMPDA)) {
							future = new TermSet(followSet.get(B));
							current = new TermSet(followSet.get(B));
							future.add(followSet.get(p.getLHSSymbol()));
							if(!current.containsAll(future)){
								changes = true;
							}
							followSet.get(B).add(followSet.get(p.getLHSSymbol()));
						}
					}

				}
			}
		}
		if (Debugger.DEBUG) {
			System.out.println("FillFollowSet Output : ->");
			followSet.forEach((k, v) -> System.out.println("Key : "
					+ k.getText() + " Value : " + v.toString()));
		}
	}

	private void FillFirstSet() throws Data.ErrorException {
		for (Symbol A : grammar.getNonTerminals()) {
			if (derivesLampda.get(A)) {
				firstSet.put(A, new TermSet(Symbol.LAMPDA));
			} else {
				firstSet.put(A, new TermSet(Symbol.EMPTYSET)); 
			}
		}
		if (Debugger.DEBUG) {
			System.out.println("FillFirstSet Output : ->");
			firstSet.forEach((k, v) -> System.out.println("Key : "
					+ k.getText() + " Value : " + v.toString()));
		}
		for (Symbol a : grammar.getTerminals()) {
			firstSet.put(a, new TermSet(a));
			for (Symbol A : grammar.getNonTerminals()) {
				for (Production p : grammar.getNonTerminalProductionList(A)) {
					if (p.getRHSSymbolNoAction(0).equals(a)) {
						firstSet.put(A, firstSet.get(A).add(a));
					}
				}
			}
		}
		if (Debugger.DEBUG) {
			System.out.println("FillFirstSet Output : ->");
			firstSet.forEach((k, v) -> System.out.println("Key : "
					+ k.getText() + " Value : " + v.toString()));
		}
		boolean changes = true;
		while (changes) {
			changes = false;
			for (Production p : grammar.getProductions()) {
				Symbol key = p.getLHSSymbol();
				if (hasChanged(new Production(p.getRHSNoAction()), key)) {
					changes = true;
				}
				firstSet.put(key,
						firstSet.get(key).setAdd(ComputerFirst(p.getRHSNoAction())));
			}
		}
		if (Debugger.DEBUG) {
			System.out.println("FillFirstSet Output : ->");
			firstSet.forEach((k, v) -> System.out.println("Key : "
					+ k.getText() + " Value : " + v.toString()));
		}
		return;
	}

	private boolean hasChanged(Production p, Symbol key) throws Data.ErrorException {
		boolean result = false;
		TermSet temp = new TermSet();
		TermSet previous = new TermSet();
		temp = ComputerFirst(p.getAllNoActionSymbols());
		previous = firstSet.get(key);
		if (!previous.getSet().containsAll(temp.getSet())) {
			result = true;
		}
		return result;
	}

	private TermSet ComputerFirst(ArrayList<Symbol> x) {
		int i = 0, k = 0;
		TermSet result = new TermSet();

		k = x.size();
		if (k == 0) {
			result.add(Symbol.LAMPDA);
		} else {
			result = firstSet.get(x.get(0)).setSubstract(Symbol.LAMPDA); 
			i = 0;
			while (i < k && firstSet.get(x.get(i)).contains(Symbol.LAMPDA)) {
				result = result.add(firstSet.get(x.get(i)).setSubstract(
						Symbol.LAMPDA));
				++i;
			}
			if (i == k && firstSet.get(x.get(k - 1)).contains(Symbol.LAMPDA)) {
				result.add(Symbol.LAMPDA);
			}
		}

		return result;
	}

	private MarkedVocabulary MarkLambda() throws Data.ErrorException {
		derivesLampda = new MarkedVocabulary();
		boolean changes = true;
		boolean RHS_DerivesLambda;

		for (Symbol s : grammar.getVocabulary()) {
			if (s.equals(Symbol.LAMPDA)) {
				derivesLampda.put(s, true);
			} else {
				derivesLampda.put(s, false);
			}
		}
		while (changes) {
			changes = false;
			for (Production p : grammar.getProductions()) {
				RHS_DerivesLambda = true;
				for (int i = 0; i < p.getRHSLengthNoActionSymbols(); ++i) {
					RHS_DerivesLambda = RHS_DerivesLambda
							& derivesLampda.get(p.getRHSSymbolNoAction(i));
				}
				if (RHS_DerivesLambda && !derivesLampda.get(p.getLHSSymbol())) {
					changes = true;
					derivesLampda.put(p.getLHSSymbol(), true);
				}
			}
		}
		if (Debugger.DEBUG) {
			derivesLampda.getMap().forEach(
					(k, v) -> System.out.println("Key : " + k.getText()
							+ " Value : " + v));
		}
		return derivesLampda;

	}

	private void checkExceptions() throws Exception {
		int symbolIndex = 0;
		for (int i = 0; i < grammar.size(); ++i) {
			symbolIndex = grammar.getProduction(i).contains(
					new Symbol(false, true, true, false, "lampda"));
			if (symbolIndex > -1) {
				grammar.getProduction(i).set(symbolIndex,
						new Symbol(false, true, false, false, "lampda"));
			}
		}
		return;
	}

	private boolean hasSpecialCharacter(char c) {
		if (c >= (char) 33 && c <= (char) 126)
			return true;
		return false;
	}

	private void print(ArrayList<String> items) {
		for (String s : items) {
			System.out.println(s);
		}
	}
	
	@Override
	public String toString() {
		return grammar.toString();
	}

	public String getProductionAndSymbolDetails() {
		return grammar.getProductionAndSymbolDetails();
	}

}
