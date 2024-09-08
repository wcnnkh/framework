package io.basc.framework.util.observe;

import java.io.Serializable;

public class SharedReceipt implements Receipt, Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean done;
	private final Throwable cause;

	/**
	 * 一个成功的回执
	 */
	public SharedReceipt() {
		this(true, null);
	}

	/**
	 * 一个失败的回执
	 * 
	 * @param cause
	 */
	public SharedReceipt(Throwable cause) {
		this(true, cause);
	}

	public SharedReceipt(boolean done, Throwable cause) {
		this.done = done;
		this.cause = cause;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public boolean isSuccess() {
		return done && cause == null;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

}
