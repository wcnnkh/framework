package io.basc.framework.util;

import java.util.Collections;
import java.util.List;

public class EmptyResultSet<E> implements ResultSet<E> {

	@Override
	public Cursor<E> iterator() {
		return Cursor.empty();
	}

	@Override
	public List<E> toList() {
		return Collections.emptyList();
	}

}
