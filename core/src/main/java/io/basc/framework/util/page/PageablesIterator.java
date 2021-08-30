package io.basc.framework.util.page;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.Supplier;

public class PageablesIterator<K, T> extends AbstractIterator<Pageables<K, T>> {
	private Pageables<K, T> pageables;
	private Supplier<Pageables<K, T>> current;

	public PageablesIterator(Pageables<K, T> pageables) {
		this.pageables = pageables;
		this.current = new StaticSupplier<Pageables<K, T>>(pageables);
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		return pageables.hasNext();
	}

	@Override
	public Pageables<K, T> next() {
		if (current != null) {
			Pageables<K, T> value = current.get();
			current = null;
			return value;
		} else {
			this.pageables = pageables.next();
			return pageables;
		}
	}
}
