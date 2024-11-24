package io.basc.framework.util;

import lombok.NonNull;

/**
 * 回执
 * 
 * @author shuchaowen
 *
 */
public interface Receipt extends Registration {
	public static final Receipt FAILURE = new Receipted(false, null);
	public static final Receipt SUCCESS = new Receipted(true, null);

	public static Receipt failure(Throwable cause) {
		return new Receipted(false, cause);
	}

	public static Receipt success(Throwable cause) {
		return new Receipted(true, cause);
	}

	public static Receipt success(@NonNull Registration registration) {
		return new SuccessfullyRegistered(registration);
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
