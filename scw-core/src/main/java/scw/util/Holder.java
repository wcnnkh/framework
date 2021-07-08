package scw.util;

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
}
