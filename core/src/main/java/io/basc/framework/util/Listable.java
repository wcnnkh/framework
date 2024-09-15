package io.basc.framework.util;

/**
 * 可列出的
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Listable<E> {
	/**
	 * 列出所有元素
	 * 
	 * @return
	 */
	Elements<E> getElements();

	/**
	 * 是否是空的
	 * 
	 * @return
	 */
	default boolean isEmpty() {
		return getElements().isEmpty();
	}
}
