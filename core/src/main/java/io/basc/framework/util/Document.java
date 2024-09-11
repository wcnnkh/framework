package io.basc.framework.util;

/**
 * 一个对文档抽象的定义
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Document<E> {
	/**
	 * 获取所有元素
	 * 
	 * @return
	 */
	Elements<E> getElements();

	default boolean isEmpty() {
		return getElements().isEmpty();
	}
}
