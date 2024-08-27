package io.basc.framework.util.select;

import io.basc.framework.util.Elements;

/**
 * 合并者
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Merger<E> {
	/**
	 * 将多个合并为一个
	 * 
	 * @param elements
	 * @return
	 */
	E merge(Elements<? extends E> elements);
}
