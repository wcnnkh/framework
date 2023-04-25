package io.basc.framework.core;

import java.util.function.Function;

import io.basc.framework.util.Elements;

/**
 * 成员列表
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface Members<E> {
	/**
	 * 来源
	 * 
	 * @return
	 */
	ResolvableType getSource();

	/**
	 * 列表
	 * 
	 * @return
	 */
	Elements<E> getElements();

	<T> Members<T> convert(Function<? super Elements<E>, ? extends Elements<T>> converter);
}
