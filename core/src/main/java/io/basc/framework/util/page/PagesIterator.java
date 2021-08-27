package io.basc.framework.util.page;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.Supplier;

public class PagesIterator<T> extends AbstractIterator<Pages<T>> {
	private Pages<T> pages;
	private Supplier<Pages<T>> current;

	public PagesIterator(Pages<T> pages) {
		this.pages = pages;
		this.current = new StaticSupplier<Pages<T>>(pages);
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		return pages.hasNext();
	}

	@Override
	public Pages<T> next() {
		if (current != null) {
			Pages<T> value = current.get();
			current = null;
			return value;
		} else {
			this.pages = pages.next();
			return pages;
		}
	}
}
