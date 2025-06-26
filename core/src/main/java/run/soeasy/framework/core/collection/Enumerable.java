package run.soeasy.framework.core.collection;

import java.util.Enumeration;

/**
 * 可枚举的
 * 
 * @see Iterable
 * @author soeasy.run
 *
 * @param <E>
 */
@FunctionalInterface
public interface Enumerable<E> {
	Enumeration<E> enumeration();
}
