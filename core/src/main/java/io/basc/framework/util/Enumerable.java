package io.basc.framework.util;

import java.util.Enumeration;

/**
 * 可枚举的
 * 
 * @see Iterable
 * @author wcnnkh
 *
 * @param <E>
 */
@FunctionalInterface
public interface Enumerable<E> {
	Enumeration<E> enumeration();
}
