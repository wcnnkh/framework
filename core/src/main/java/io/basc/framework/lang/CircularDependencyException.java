package io.basc.framework.lang;

/**
 * 循环依赖异常
 * 
 * @author wcnnkh
 *
 */
public class CircularDependencyException extends IllegalStateException {
	private static final long serialVersionUID = 1L;

	public CircularDependencyException() {
		super();
	}

	public CircularDependencyException(String s) {
		super(s);
	}

	public CircularDependencyException(String message, Throwable cause) {
		super(message, cause);
	}
}
