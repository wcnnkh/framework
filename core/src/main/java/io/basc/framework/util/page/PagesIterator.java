package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.function.Supplier;

import io.basc.framework.util.StaticSupplier;

final class PagesIterator<K, T> implements Iterator<Pages<K, T>> {
	private Pages<K, T> pages;
	private Supplier<Pages<K, T>> current;

	public PagesIterator(Pages<K, T> pages) {
		this.pages = pages;
		this.current = new StaticSupplier<Pages<K, T>>(pages);
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		return pages.hasNext();
	}

	@Override
	public Pages<K, T> next() {
		if (current != null) {
			Pages<K, T> value = current.get();
			current = null;
			return value;
		} else {
			this.pages = pages.next();
			return pages;
		}
	}
}
