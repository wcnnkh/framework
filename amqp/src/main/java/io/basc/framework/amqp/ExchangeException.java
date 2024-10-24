package io.basc.framework.amqp;

import io.basc.framework.lang.NestedRuntimeException;

public class ExchangeException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public ExchangeException(String msg) {
		super(msg);
	}

	public ExchangeException(Throwable cause) {
		super(cause);
	}

	public ExchangeException(String message, Throwable cause) {
		super(message, cause);
	}

}
