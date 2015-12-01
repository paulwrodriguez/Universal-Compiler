package Data;

public class ErrorException extends Exception{
	private String m;
	public ErrorException(String m){
		this.m = m;
	}
	public ErrorException(String m, int code){
		this.m = m;
	}
	@Override
	public String getMessage(){ return this.m; 	}
}
