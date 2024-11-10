package io.basc.framework.sequences;

/**
 * 序列的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Sequence<T> {
	/**
	 * 获取下一个
	 * 
	 * @return
	 */
	T next();
}
