package io.basc.framework.util;

/**
 * 回执
 * 
 * @author shuchaowen
 *
 */
public interface Receipt extends Registration {
	public static final Receipt SUCCESS_RECEIPT = new Receipted(true, null);

	public static Receipt fail(Throwable cause) {
		return new Receipted(false, cause);
	}

	public static Receipt fail() {
		return fail(null);
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

	/**
	 * 
	 * 同步
	 * 
	 * @return 返回一个同步后的回执
	 */
	default Receipt sync() {
		return this;
	}
}
