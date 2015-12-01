import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.charset.Charset;


public class FileReader  {
	
	private static final int BUFFERSIZE = 50;
	private InputStream in;
	private Reader reader;
	private PushbackReader pbr;
	String fileName = "";
	
	FileReader(String fileName) throws IOException {
		this.fileName = fileName; // TODO save filename for later
		try {
			in = new FileInputStream(fileName);
			reader = new InputStreamReader(in, Charset.defaultCharset());
	        // buffer for efficiency
	        pbr = new PushbackReader(reader, BUFFERSIZE);
		} catch (FileNotFoundException e) {
			close(); 
			e.printStackTrace();
			System.out.println("\nThere was an issue opening file -> " + this.fileName);
			throw e;
		} 
	}
	public void resetStream() throws IOException{
		try {
			in = new FileInputStream(fileName);
			reader = new InputStreamReader(in, Charset.defaultCharset());
	        // buffer for efficiency
	        pbr = new PushbackReader(reader, BUFFERSIZE);
		} catch (FileNotFoundException e) {
			close(); 
			e.printStackTrace();
			System.out.println("\nThere was an issue opening file -> " + this.fileName);
			throw e;
		} 
	}
	// Post: Returns -1 if EOF is found on first go. then returns -1 for all sub calls
	public int Read() throws IOException {
		try {
			if(!pbr.ready()) { throw new IOException("ErrorException: Cannot read past EOF"); }
			return pbr.read();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	public int Inspect() throws IOException {
		int c = pbr.read();
		if(c != -1) {
//			int r = Read();
			pushBack(c);
			return c;
		}
		else 
			return -1; // TODO put a better return error code. maybe constant
		
	}
	// Post: The next input character is removed, but not returned; no effect at end of file
	public void Advance() throws IOException {
		if(Inspect() == -1) {
			return; // do nothing
		}
		else {
			Read();
		}
	}
	// Post: Return True if FileReader is at EOF, i.e. EOF has been reached
	public boolean EOF() throws IOException {
		try {
			return !pbr.ready(); // TODO making assumption that ready is only not ready when eof
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e = new IOException (e.getMessage() + "ERROR: Something went wrong with FileReader.EOF" );
			throw e;
		}
	}
	public void pushBack(int c) throws IOException {
		try {
			pbr.unread(c);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ErrorException with pushBack. Value -> " + c);
			throw e;
		}
	}
	public void close() throws IOException {
		if(in != null)
			in.close();
		if(reader != null)
			reader.close();
	}
	/*public void pushBack(char[] charArray) {
		try {
			for(char c : charArray) {
				System.out.print(c);
			}
			System.out.println();
			pbr.unread(charArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}*/
}
