package run.soeasy.framework.util.function;

import java.util.Iterator;

final class ParentIterator<T extends ParentDiscover<T>> implements Iterator<T> {
	private ParentDiscover<T> parent;

	ParentIterator(ParentDiscover<T> parent) {
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
