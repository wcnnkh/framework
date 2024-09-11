package io.basc.framework.util.observe;

import java.io.Serializable;

/**
 * 最终状态的回执
 * 
 * @author shuchaowen
 *
 */
public class FinalStateReceipt extends Cancelled implements Receipt, Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean done;
	private final Throwable cause;

	/**
	 * 一个成功的回执
	 */
	public FinalStateReceipt() {
		this(true, null);
	}

	/**
	 * 一个失败的回执
	 * 
	 * @param cause
	 */
	public FinalStateReceipt(Throwable cause) {
		this(true, cause);
	}

	public FinalStateReceipt(boolean done, Throwable cause) {
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

	@Override
	public boolean isCancelled() {
		return false;
	}
}
