package io.basc.framework.util;

/**
 * 回执
 * 
 * @author shuchaowen
 *
 */
public interface Receipt extends Registration {
	public static final Receipt SUCCESS_RECEIPT = new FinalStateReceipt();

	public static Receipt fail(Throwable cause) {
		return new FinalStateReceipt(cause);
	}

	public static Receipt success() {
		return SUCCESS_RECEIPT;
	}

	/**
	 * 异常信息
	 * 
	 * @return
	 */
	Throwable cause();

	/**
	 * 是否已完成
	 * 
	 * @return
	 */
	boolean isDone();

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	boolean isSuccess();
}
