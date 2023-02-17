package io.basc.framework.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class StandardResultSet<E> implements ResultSet<E> {
	private transient final Supplier<? extends Cursor<E>> cursorSupplier;
	private volatile Iterable<? extends E> iterable;

	public StandardResultSet(Iterable<? extends E> iterable) {
		this.cursorSupplier = null;
		this.iterable = iterable;
	}

	public StandardResultSet(Supplier<? extends Cursor<E>> cursorSupplier) {
		this.cursorSupplier = cursorSupplier;
	}

	@Override
	public Cursor<E> iterator() {
		if (iterable != null) {
			return Cursor.of(iterable);
		}
		return cursorSupplier.get();
	}

	@Override
	public E last() {
		if (iterable instanceof List) {
			List<? extends E> list = (List<? extends E>) iterable;
			return list.get(list.size() - 1);
		}
		return ResultSet.super.last();
	}

	@Override
	public List<E> toList() {
		if (iterable == null && !(iterable instanceof List)) {
			synchronized (this) {
				if (iterable == null && !(iterable instanceof List)) {
					iterable = ResultSet.super.toList();
				}
			}
		}
		return Collections.unmodifiableList((List<? extends E>) iterable);
	}
}
