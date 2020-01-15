package scw.net.message;

import scw.lang.NestedRuntimeException;

public class MessageConvetException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public MessageConvetException(Throwable cause) {
		super(cause);
	}

	public MessageConvetException(String message, Throwable cause) {
		super(message, cause);
	}
}
