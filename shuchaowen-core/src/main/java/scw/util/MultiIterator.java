package scw.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * 将多个迭代器合并
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public final class MultiIterator<E> implements Iterator<E> {
	private Iterator<Iterator<? extends E>> iteratorIterator;
	private Iterator<? extends E> iterator;

	public MultiIterator(Collection<Iterator<? extends E>> iterators) {
		if (iterators != null) {
			this.iteratorIterator = iterators.iterator();
		}
	}
	
	public boolean hasNext() {
		if (iteratorIterator == null) {
			return false;
		}

		if (iterator == null || !iterator.hasNext()) {
			if (iteratorIterator.hasNext()) {
				iterator = iteratorIterator.next();
			} else {
				return false;
			}
		}

		if (iterator.hasNext()) {
			return true;
		}
		return hasNext();
	}

	public E next() {
		if (iterator == null) {
			throw new UnsupportedOperationException("Call the hasnext method first");
		}

		return iterator.next();
	}

}
