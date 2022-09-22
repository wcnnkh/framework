package io.basc.framework.factory;

public class FactoryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FactoryException(String message) {
		super(message);
	}

	public FactoryException(Throwable e) {
		super(e);
	}

	public FactoryException(String message, Throwable e) {
		super(message, e);
	}
}
