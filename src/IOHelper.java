import java.io.PrintWriter;


public class IOHelper {
	public Scanner sc;
	public PrintWriter pw;
	public PrintWriter[] pwArray;
	public static final int SYS = 1; 
	
	IOHelper(Scanner sc, PrintWriter pw) {
		this.sc = sc;
		this.pw = pw;
	}
	IOHelper(Scanner sc, PrintWriter ... pwArray) {
		this.sc = sc;
		this.pw = pwArray[0]; // TODO fix this
		this.pwArray = pwArray;
	}
	public void println(int out, String data){
		if(out > pwArray.length ) {
			System.out.println("ErrorException: IOHelper.Println -> " + out + " > " + pwArray.length);
		}
		pwArray[out].println(data);
	}
	void close() {
		if(pw != null) {
			pw.close();
		}
		if(pwArray != null) {
			for(int i = 0; i < pwArray.length; ++i) {
				if(i == SYS) {
					pwArray[i].flush();
					continue;
				}
				pwArray[i].close();
				
			}
		}
	}
	
}
