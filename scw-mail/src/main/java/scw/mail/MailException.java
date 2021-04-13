package scw.mail;

import scw.lang.NestedRuntimeException;

public class MailException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public MailException(String msg) {
		super(msg);
	}

	public MailException(Throwable cause) {
		super(cause);
	}

	public MailException(String message, Throwable cause) {
		super(message, cause);
	}
}
