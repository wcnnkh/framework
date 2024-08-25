package io.basc.framework.util.event;

public class EventPushException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EventPushException(String message) {
		super(message);
	}

	public EventPushException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventPushException(Throwable cause) {
		super(cause);
	}
}
