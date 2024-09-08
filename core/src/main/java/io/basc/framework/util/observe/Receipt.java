package io.basc.framework.util.observe;

/**
 * 回执
 * 
 * @author shuchaowen
 *
 */
public interface Receipt {
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
	 * 异常信息
	 * 
	 * @return
	 */
	Throwable cause();
}
