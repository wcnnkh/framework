package io.basc.framework.lucene;

import io.basc.framework.data.repository.RepositoryException;

public class LuceneException extends RepositoryException {
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
