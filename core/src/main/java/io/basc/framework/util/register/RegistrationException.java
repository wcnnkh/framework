package io.basc.framework.util.register;

/**
 * 事件注册异常
 * 
 * @author wcnnkh
 *
 */
public class RegistrationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RegistrationException(String message) {
		super(message);
	}

	public RegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegistrationException(Throwable cause) {
		super(cause);
	}
}
