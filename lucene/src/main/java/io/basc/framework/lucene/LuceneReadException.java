package io.basc.framework.lucene;

public class LuceneReadException extends LuceneException {
	private static final long serialVersionUID = 1L;

	public LuceneReadException(Throwable e) {
		super(e);
	}

	public LuceneReadException(String message) {
		super(message);
	}

	public LuceneReadException(String message, Throwable cause) {
		super(message, cause);
	}
}
