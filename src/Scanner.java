/*
 * Requirements:
 * 	1) CheckException will need to be updated as new fdatables are created
 * 	2) FileName for constructor is file that will be read from. 
 * 	3) Reserved words need to be updated in TokenConstants class if changes to grammar exist
 */
import java.io.IOException;

import Tables.FDATable;
import Data.Actions;
import Data.TokenConstants;
import Data.TokenCode;
import Data.TokenText;


public class Scanner {
	 /*Public*/ 
	public static final int EOF = -99;
	public static final int EOFI = EOF;
	public static final String EOFS = "$";
	 /*Constructor*/
	Scanner(String fileName) throws IOException {
		fr = new FileReader(fileName); // TODO do something if there is an error
		table = new FDATable();
	}

	Scanner() {
		// TODO empty constructor for testing. make private later
	}
	public void resetScanner() throws IOException{
		fr.resetStream();
	}
	// Post: Returns next TokenConstants in file. 
	public String Scan(TokenCode tokenCode, TokenText tokenText) throws Exception  {
		isException = false;
		table.state = table.StartState;
		tokenText.value = "";
		while (!table.isInFinalState() && !EOF()) {
			switch(Action(table.state, CurrentChar())) {
			case Error:
				System.out.println("ERROR From Scanner. #Do lexical ErrorException Recovery");
				System.exit(-1);
			case MoveAppend:
				table.state = NextState(table.state, CurrentChar());
				tokenText.value = tokenText.value + CurrentChar();
				ConsumeChar();
				break;
			case MoveNoAppend:
				table.state = NextState(table.state, CurrentChar());
				ConsumeChar();
				break;
			case HaltAppend:
				table.state = NextState(table.state, CurrentChar());
				LoopUpCode(table.state, CurrentChar(), tokenCode);
				tokenText.value = tokenText.value + CurrentChar();
				CheckExceptions(tokenCode, tokenText);
				ConsumeChar();
				return getToken() ;
			case HaltNoAppend:
				table.state = NextState(table.state, CurrentChar());
				LoopUpCode(table.state, CurrentChar(), tokenCode);
				CheckExceptions(tokenCode, tokenText);
				ConsumeChar();
				if(tokenCode.code == 0) {
					Scan(tokenCode, tokenText);
				}
				return getToken();
			case HaltReuse:
				table.state = NextState(table.state, CurrentChar());
				LoopUpCode(table.state, CurrentChar(), tokenCode);
				CheckExceptions(tokenCode, tokenText);
				if(tokenCode.code == 0){
					Scan(tokenCode, tokenText);
				}
				return getToken(); 
			default:
				System.out.println("ERROR: Scan.DefaultCase. Reached Unreachable Code");
				System.exit(-1);
			}
		}
		/*
		 * test edge case
		 */
		if(tokenText.value == ""){
			tokenCode.code = EOFI;
			tokenText.value = EOFS;
			return EOFS;
		}
		if(EOF()) {
			table.state = NextState(table.state, (char) FDATable.StatesList.EOL); // TODO REMOVE THIS HACK
			table.nextState = table.state;
			
		}
		else {
			table.state = NextState(table.state, CurrentChar());
			
		}
		LoopUpCode(table.state, CurrentChar(), tokenCode);
		CheckExceptions(tokenCode, tokenText);
		if(tokenCode.code == 0){
			Scan(tokenCode, tokenText); // should not be ? 
		}
		if(isException) {
			return getToken();
		}
		// error condition 
		tokenCode.code = EOFI;
//		tokenText.value = EOFS;
		return Scanner.EOFS;
	}

	private boolean EOF() throws IOException {
		if(fr.Inspect() == -1){
			return true; // EOF has been reached
		}
		else {
			return false;
		}
	}

	private void CheckExceptions(TokenCode tokenCode, TokenText tokenText) {
		if(tokenText.value.equalsIgnoreCase("begin")){
			isException = true;
			tokenString = TokenConstants.BEGINSYM.toString();
			tokenCode.code = 1;
			return;
		}
		else if(tokenText.value.equalsIgnoreCase("end")){
			isException = true;
			tokenString = TokenConstants.ENDSYM.toString();
			tokenCode.code = 2;
			return;
		}
		else if(tokenText.value.equalsIgnoreCase("read")){
			isException = true;
			tokenString = TokenConstants.READSYM.toString();
			tokenCode.code = 3;
			return;
		}
		else if(tokenText.value.equalsIgnoreCase("write")){
			isException = true;
			tokenString = TokenConstants.WRITESYM.toString();
			tokenCode.code = 4;
			return;
		}
		else if(tokenText.value.equalsIgnoreCase("declare")){
			isException = true;
			tokenString = TokenConstants.DECLARESYM.toString();
			tokenCode.code = 5;
			return;
		}
		else if(tokenText.value.equalsIgnoreCase("integer")){
			isException = true;
			tokenString = TokenConstants.INTEGERSYM.toString();
			tokenCode.code = 6;
			return;
		}
		else {
			return;
		}	
	}

	private void LoopUpCode(int state, char currentChar, TokenCode tokenCode) {
		// Put TokenCode as state
		// put states that you want to make it look back throught if found to end here. e.g. space tokens or comment tokens
		if(state == 15 || state == 13 ) {
			tokenCode.code = 0;
			return;
		}
		tokenCode.code = table.getTokenCode();
		return;
	}
	private String getToken() {
		if(isException){
			return tokenString;
		}
		return table.getToken();
	}
	private int NextState(int state, char currentChar) {
		return table.NextState(state, currentChar);
	}

	private Actions Action(int state, char c) {
		Actions nextState = table.Action(c);
		return nextState;
	}

	private void ConsumeChar() {
		try {
			fr.Advance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: ConsumeChar Advance");
			e.printStackTrace();
		}
	}

	private char CurrentChar() {
		char c = 0;
		try {
			c = (char) fr.Inspect();
			return c;
		} catch (IOException e) {
			System.out.println("ERROR: currentChar->" + c);
			e.printStackTrace();
			return (char) -1;
		}

	}


	/*Private*/
	private void LexicalError(int errorChar) throws Exception {
		throw new Exception("There was a Lexical ErrorException at " + (char)errorChar);
	}
	
	
	public void close() throws IOException {
		fr.close();
	}
	
	 /*Private Data Members */
	private boolean isException;
	private String tokenString;
	private FileReader fr;
	private FDATable table;
}
