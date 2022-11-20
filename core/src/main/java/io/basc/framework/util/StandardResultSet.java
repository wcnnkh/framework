package io.basc.framework.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class StandardResultSet<E> implements ResultSet<E> {
	private transient final Supplier<? extends Cursor<E>> cursorSupplier;
	private volatile List<? extends E> list;

	public StandardResultSet(List<? extends E> list) {
		this.cursorSupplier = null;
		this.list = list;
	}

	public StandardResultSet(Supplier<? extends Cursor<E>> cursorSupplier) {
		this.cursorSupplier = cursorSupplier;
	}

	@Override
	public Cursor<E> iterator() {
		if (list != null) {
			return Cursor.create(list.iterator());
		}
		return cursorSupplier.get();
	}

	@Override
	public E last() {
		if (list != null) {
			return list.get(list.size() - 1);
		}
		return ResultSet.super.last();
	}

	@Override
	public List<E> toList() {
		if (list == null) {
			synchronized (this) {
				if (list == null) {
					list = ResultSet.super.toList();
				}
			}
		}
		return Collections.unmodifiableList(list);
	}
}
