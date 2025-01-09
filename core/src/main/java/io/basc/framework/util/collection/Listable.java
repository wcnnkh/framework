package io.basc.framework.util.collection;

import io.basc.framework.util.function.Wrapper;

/**
 * 可列出的
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Listable<E> {
	public static interface ListableWrapper<E, W extends Listable<E>> extends Listable<E>, Wrapper<W> {
		@Override
		default Elements<E> getElements() {
			return getSource().getElements();
		}

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}
	}

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
