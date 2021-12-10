package io.basc.framework.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 支持去重的迭代器
 * 
 * @see HashSet
 * @author shuchaowen
 *
 * @param <E>
 */
public class DuplicateRemovalIterator<E> extends AbstractIterator<E> {
	private Set<E> set;
	private final Iterator<E> iterator;
	private Supplier<E> cacheSupplier;

	public DuplicateRemovalIterator(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	public boolean hasNext() {
		if (cacheSupplier != null) {
			return true;
		}

		if (iterator == null || !iterator.hasNext()) {
			set = null;
			return false;
		}

		if (set == null) {
			set = new HashSet<E>();
		}

		E item = iterator.next();
		if (!set.add(item)) {
			return hasNext();
		}
		cacheSupplier = new StaticSupplier<E>(item);
		return true;
	}

	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		E item = cacheSupplier.get();
		cacheSupplier = null;
		return item;
	}

}
