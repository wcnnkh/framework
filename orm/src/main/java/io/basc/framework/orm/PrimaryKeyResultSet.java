package io.basc.framework.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.Cursor;

public class PrimaryKeyResultSet<K, V> extends ObjectResultSet<V> {
	private final List<? extends K> inPrimaryKeys;
	private final Object[] primaryKeys;

	public PrimaryKeyResultSet(Supplier<? extends Cursor<V>> cursorSupplier, ObjectKeyFormat objectKeyFormat,
			Structure<? extends Property> structure, List<? extends K> inPrimaryKeys, Object... primaryKeys) {
		super(cursorSupplier, objectKeyFormat, structure);
		this.inPrimaryKeys = inPrimaryKeys;
		this.primaryKeys = primaryKeys;
	}

	public Collection<? extends K> getInPrimaryKeys() {
		return inPrimaryKeys;
	}

	public Object[] getPrimaryKeys() {
		return primaryKeys;
	}

	public Map<K, V> toMap() {
		return toMap(inPrimaryKeys, primaryKeys);

	}
}
