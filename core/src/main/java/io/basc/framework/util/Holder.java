package io.basc.framework.util;

/**
 * 持有者定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Holder<T> extends Status<T> {
	/**
	 * 释放
	 * 
	 * @return
	 */
	boolean release();

	/**
	 * 这个持有者是否是活跃的
	 * 
	 * @see AbstractHolder#isActive()
	 */
	@Override
	boolean isActive();
}
