package scw.net.message.converter;

import scw.lang.NestedRuntimeException;

public class MessageConvertException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public MessageConvertException(Throwable cause) {
		super(cause);
	}

	public MessageConvertException(String message, Throwable cause) {
		super(message, cause);
	}
}
