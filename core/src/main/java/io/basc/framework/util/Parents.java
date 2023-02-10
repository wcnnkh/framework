package io.basc.framework.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class Parents<T extends ParentDiscover<T>> implements ReversibleIterator<T> {
	private ListIterator<T> listIterator;
	private ParentDiscover<T> parent;

	Parents(ParentDiscover<T> parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasNext() {
		if (listIterator != null) {
			return listIterator.hasNext();
		}
		return parent != null && parent.hasParent();
	}

	@Override
	public T next() {
		if (listIterator != null) {
			return listIterator.next();
		}
		T parent = this.parent.getParent();
		this.parent = parent;
		return parent;
	}

	@Override
	public boolean hasPrevious() {
		if (listIterator == null) {
			listIterator = toList().listIterator();
		}
		return listIterator.hasPrevious();
	}

	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException(Parents.class.getName() + "#previous");
		}
		return listIterator.previous();
	}
}
