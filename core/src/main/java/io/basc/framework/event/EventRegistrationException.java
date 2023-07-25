package io.basc.framework.event;

import io.basc.framework.util.registry.RegistrationException;

/**
 * 事件注册异常
 * 
 * @author wcnnkh
 *
 */
public class EventRegistrationException extends RegistrationException {
	private static final long serialVersionUID = 1L;

	public EventRegistrationException(String message) {
		super(message);
	}

	public EventRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventRegistrationException(Throwable cause) {
		super(cause);
	}
}
