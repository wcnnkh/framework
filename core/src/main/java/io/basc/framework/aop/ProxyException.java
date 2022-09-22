package io.basc.framework.aop;

public class ProxyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProxyException(String message) {
		super(message);
	}

	public ProxyException(Throwable e) {
		super(e);
	}

	public ProxyException(String message, Throwable e) {
		super(message, e);
	}
}