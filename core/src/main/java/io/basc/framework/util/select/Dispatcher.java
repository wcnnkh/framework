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
	Elements<E> dispatch(Elements<? extends E> elements);
}
