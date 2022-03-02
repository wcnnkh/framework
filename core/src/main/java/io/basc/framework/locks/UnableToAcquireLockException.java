package io.basc.framework.locks;

/**
 * 无法获取锁
 * 
 * @author wcnnkh
 *
 */
public class UnableToAcquireLockException extends LockException {
	private static final long serialVersionUID = 1L;

	public UnableToAcquireLockException(String msg) {
		super(msg);
	}

	public UnableToAcquireLockException(Throwable cause) {
		super(cause);
	}

	public UnableToAcquireLockException(String message, Throwable cause) {
		super(message, cause);
	}
}
