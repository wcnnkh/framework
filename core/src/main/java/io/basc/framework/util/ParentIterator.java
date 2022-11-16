package io.basc.framework.util;

import java.util.Iterator;

public class ParentIterator<T extends ParentDiscover<T>> implements Iterator<T> {
	private ParentDiscover<T> parent;

	public ParentIterator(ParentDiscover<T> parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasNext() {
		return parent != null && parent.hasParent();
	}

	@Override
	public T next() {
		T parent = this.parent.getParent();
		this.parent = parent;
		return parent;
	}
}
