package io.basc.framework.data.repository;

import io.basc.framework.data.DataException;

public class RepositoryException extends DataException {
	private static final long serialVersionUID = 1L;

	public RepositoryException(String msg) {
		super(msg);
	}

	public RepositoryException(Throwable cause) {
		super(cause);
	}

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
