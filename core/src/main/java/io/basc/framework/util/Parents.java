package io.basc.framework.util;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

final class Parents<T extends ParentDiscover<T>> implements ReversibleIterator<T> {
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
			List<T> list = toList();
			listIterator = list.listIterator(list.size());
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
