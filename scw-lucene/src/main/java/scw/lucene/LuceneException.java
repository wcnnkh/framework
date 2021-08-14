package scw.lucene;

public class LuceneException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public LuceneException(Throwable e) {
		super(e);
	}
	
	public LuceneException(String message) {
	    super(message);
	}
	
	public LuceneException(String message, Throwable cause) {
	    super(message, cause);
	}
}
