package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.function.Supplier;

public class PageableIterator<K, T> implements Iterator<Pageable<K, T>> {
	private Pageables<K, T> pageables;
	private Supplier<Pageable<K, T>> current;

	public PageableIterator(Pageables<K, T> pageables) {
		this.pageables = pageables;
		this.current = () -> pageables;
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		return pageables.hasNext();
	}

	@Override
	public Pageable<K, T> next() {
		if (current != null) {
			Pageable<K, T> value = current.get();
			current = null;
			return value;
		} else {
			this.pageables = this.pageables.next();
			return pageables;
		}
	}
}
