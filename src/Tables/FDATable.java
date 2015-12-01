/*
 * Paul Rodriguez
 * Data in file requirements
 * 	any horizontal table not a single character will need specific implementation
 * 	may add functionality for letter digit and blank
 * 
 * How it works:
 * works on nextstate basis. if nextstate is finalstate then return token will return the token correcty
 * 
 * Requirements:
 * 	1) Statelist enum needs to be updated with every new state file when implemented
 * 	2) getindexfromchar will also need to be updated with every new state transition file
 * 	3) action will need updating based on fda state file
 */
package Tables;

import Data.Actions;
import Debug.Debugger;



/*Micro Language Table*/
/* State | Character set classes (ASCII or EBCDIC)
|Letter Digit Blank + — = : , ; ( ) _Tab Eol Other
|
0 1 2 3 14 4 6 17 18 19 20 3 3
1 1 1 11 11 11 11 11 11 11 11 11 1 11 11
2 12 2 12 12 12 12 12 12 12 12 12 12 12 12
3 13 13 3 13 13 13 13 13 13 13 13 13 3 3
4 21 21 21 21 5 21 21 21 21 21 21 21 21
5 5 5 5 5 5 5 5 5 5 5 5 5 5 15 5
6 16
7
8
9
10
11 Id = Letter (Letter | Digit | _)*
12 IntLiteral = Digit+
13 EmptySpace = (Blank | Eol | Tab)+
14 PlusOp
15 Comment = — — Not(Eol)*Eol
16 AssignOp
17 Comma
18 SemiColon
19 LParen
20 Rparen
21 MinusOp */

public class FDATable {
	/* Character Constants*/
	private static final char LF = (char)10;
	private static final char CR = (char)13; 
	private static final char TAB = (char)9; 
	
	public class StatesList{
		public static final int LETTER = 0;
		public static final int DIGIT = 1;
		public static final int BLANK = 2;
		public static final int PLUS = 3;
		public static final int MINUS = 4;
		public static final int EQUALS = 5;
		public static final int COLON = 6;
		public static final int COMMA = 7;
		public static final int SEMICOLON = 8;
		public static final int LPAREN = 9;
		public static final int RPAREN = 10;
		public static final int UNDERSCORE = 11;
		public static final int TAB = 12;
		public static final int EOL = 13;
		public static final int OTHER = 14;	
	}
	private static final String NULLVALUE = "";
	
	public FDATable() {
		this.state = 0;
		this.StartState = 0;
		this.FinalStateRow = 11;
		
	}
	int getIndexFromChar(char c) {
		
		if(Character.toString(c).matches("[A-Za-z]")) {
			return StatesList.LETTER;
		}
		else if(Character.toString(c).matches("[0-9]")) {
			return StatesList.DIGIT;
		}
		else if(c == ' '){ // TODO make sure this is correct
			return StatesList.BLANK;
		}
		else if(c == '+') {
			return StatesList.PLUS;
		}
		else if(c == '-' ) {
			return StatesList.MINUS;
		}
		else if(c == '=') {
			return StatesList.EQUALS;
		}
		else if(c == ':'){
			return StatesList.COLON;
		}
		else if(c == ',') {
			return StatesList.COMMA;
		}
		else if(c == ';') {
			return StatesList.SEMICOLON;
		}
		else if(c == '(') {
			return StatesList.LPAREN;
		}
		else if(c == ')') {
			return StatesList.RPAREN;
		}
		else if(c == '_') {
			return StatesList.UNDERSCORE;
		}
		else if(c == TAB) {
			return StatesList.TAB;
		}
		else if(c == LF || c == CR ) {
			return StatesList.EOL;
		}
		else { // OTHER
			return StatesList.OTHER;
		}
		
	}
	
	public String [][] TransitionTable = new String[][] {
			{"1", "2",  "3", "14", "4", "",   "6", "17", "18", "19", "20", "", "3", "3", ""},
			{"1", "1", "11", "11","11", "11","11", "11", "11", "11", "11", "1", "11", "11", ""},
			{"12","2", "12", "12","12", "12","12", "12", "12", "12", "12", "12","12", "12", ""},
			{"13","13", "3", "13","13", "13","13", "13", "13", "13", "13", "13","3", "3", ""},
			{"21","21", "21", "21","5", "21","21", "21", "21", "21", "21", "","21", "21", ""},
			{"5","5", "5", "5","5", "5","5", "5", "5", "5", "5", "5","5", "15", "5"},
			{"","", "", "","", "16","", "", "", "", "", "","", "", ""},
			{},
			{},
			{},
			{},
			{"Id"},
			{"IntLiteral"},
			{"EmptySpace"},
			{"PlusOp"},
			{"Comment"},
			{"AssignOp"},
			{"Comma"},
			{"SemiColon"},
			{"LParen"},
			{"RParen"},
			{"MinusOp"},
	};
	public int state ;
	public int StartState;
	public int FinalStateRow;
	public int nextState = 0;
	
	public Actions Action(char c) {
		if(isAFinalState(state)) {
			return Actions.HaltReuse;
		}
		String s = TransitionTable[state][getIndexFromChar(c)];
		if(Debugger.DEBUG) {
			System.out.println("Action: c =  " + c + " | state = " + state + " | transition " + s);
		}
		if(s.equalsIgnoreCase(NULLVALUE)) {
			// There was an error 
			return Actions.Error;
		}
		else if(s.matches("\\d+")) {
			// has a next state
			nextState = 0;
			nextState = Integer.parseInt(s);
			switch(nextState) {
			case 1: 
			case 2:
			case 4: 
			case 6:
				return Actions.MoveAppend;
			case 3: 
			case 5:
				return Actions.MoveNoAppend;
			case 15:
				return Actions.HaltNoAppend;
			case 14:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
				return Actions.HaltAppend;
			case 11:
			case 12:
			case 13:
			case 21:
				return Actions.HaltReuse;
			default:
				error("ERROR: FDATable.Actions nextState = " + nextState);
				return Actions.Error;
			}
		}
		else {
			error("ERROR: FDATable transition return value  = " + s);
			return Actions.Error;
		}
	}
	private void error(String s) {
		System.out.println(s);
	}
	public boolean isInFinalState(){
		return state >= FinalStateRow;
	}
	private boolean isAFinalState(int index){
		return index >= FinalStateRow;
	}
	public int NextState(int st, char c) {
		String s = TransitionTable[st][getIndexFromChar(c)];
		if(Debugger.DEBUG){
			System.out.println("NextState st " + st + " | c " + c + " | state " + this.state + " | transition " + s);
		}
		if(s == ""){ // for EOF edge case TODO fix this EOF issue
			return -1;
		}
		return Integer.parseInt(s);
	}
	public String getToken() {
		if(isAFinalState(nextState))
			return TransitionTable[nextState][0];
		else 
			return "ERROR.getToken()";
	}
	public int getTokenCode() {
		return nextState;
	}
	
}
