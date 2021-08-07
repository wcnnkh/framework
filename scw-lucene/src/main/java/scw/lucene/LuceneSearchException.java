package scw.lucene;

public class LuceneSearchException extends LuceneReadException{
	private static final long serialVersionUID = 1L;

	public LuceneSearchException(Throwable e) {
		super(e);
	}

	public LuceneSearchException(String message, Throwable cause) {
		super(message, cause);
	}
}
