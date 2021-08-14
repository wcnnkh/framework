package scw.lucene;

public class LuceneWriteException extends LuceneException {
	private static final long serialVersionUID = 1L;

	public LuceneWriteException(Throwable e) {
		super(e);
	}

	public LuceneWriteException(String message) {
		super(message);
	}

	public LuceneWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
