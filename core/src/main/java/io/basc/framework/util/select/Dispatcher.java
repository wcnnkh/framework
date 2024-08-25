package io.basc.framework.util.select;

import io.basc.framework.util.element.Elements;

/**
 * 分发器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Dispatcher<E> {
	@SuppressWarnings("unchecked")
	public static <E> Dispatcher<E> identity() {
		return (Dispatcher<E>) IdentityDispatcher.getInstance();
	}

	Elements<E> dispatch(Elements<? extends E> elements);
}
