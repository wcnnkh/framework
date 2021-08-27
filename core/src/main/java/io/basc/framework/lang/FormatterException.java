package io.basc.framework.lang;

public class FormatterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FormatterException(String message, Throwable e) {
		super(message, e);
	}
}
