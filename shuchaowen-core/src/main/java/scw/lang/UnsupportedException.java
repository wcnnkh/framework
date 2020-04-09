package scw.lang;

public class UnsupportedException extends RuntimeException{
	private static final long serialVersionUID = 5341163945147654715L;

	public UnsupportedException(String message) {
		super(message);
	}
	
	public UnsupportedException(Throwable e) {
		super(e);
	}
	
	public UnsupportedException(Throwable e, String message) {
		super(message, e);
	}
}
