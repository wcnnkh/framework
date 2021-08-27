package io.basc.framework.lang;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5341163945147654715L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable e) {
		super(e);
	}
}
