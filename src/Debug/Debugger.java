package Debug;

import java.io.PrintWriter;

import Data.ErrorException;


public class Debugger {
	
	/*
	 * Output specific information about the program. 
	 */
	public static final boolean VOCABULARY = false; // outputs the vocabulary of the grammar
	public static final boolean GRAMMAR = false;
	
	private static final PrintWriter pw = new PrintWriter(System.out);
	
	public static void outputController(String s) {
		// Grammar output
		if(GRAMMAR){
//			PrintWriter pw = new PrintWriter(System.out);
			pw.append(s);
			pw.flush();
		}
	}
	/*
	 * *** For Output debugging information in the below sections
	 */
	public static final boolean DEBUG = false;
	public static final boolean PARSER =  false;
	public static final boolean LLCOMPILER = false;
	public static final boolean SEMANTIC = false;
	public static final boolean SYMBOLTABLE = false;
	
	public static final int TERMINATE = 0;
	public static final int FORDEBUG = 1;
	public static final int ENDPROGRAM = 2;
	public static final int UNKNOWN = 99;

	public static void ERROR(String s, int code) throws ErrorException{
		if(code == TERMINATE){
			throw new ErrorException("ErrorException encountered: " +s);
		}
		else if(code == FORDEBUG){
			//System.out.println(s);
		}
		else if(code == ENDPROGRAM){
			System.out.println(s);
			System.exit(-ENDPROGRAM);
		}
	}
	public static void ERRORNOTHROW(String s, int code){
		if(code == FORDEBUG){
			if(DEBUG)
				System.out.println(s);
		}
		else if(code == ENDPROGRAM){
			System.out.println(s);
			System.exit(-ENDPROGRAM);
		}
		else {
			System.out.println("Invalid use of ERRORNOTHROW. Program End");
			System.exit(-UNKNOWN);
		}
	}
}

