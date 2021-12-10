package io.basc.framework.util;

/**
 * 这是为了兼容1.5
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public abstract class AbstractIterator<E> implements java.util.Iterator<E> {

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}
