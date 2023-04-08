package io.basc.framework.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.Streamable;

public class PrimaryKeyElements<K, V> extends ObjectElements<V> {
	private final List<? extends K> inPrimaryKeys;
	private final Object[] primaryKeys;

	public PrimaryKeyElements(Streamable<V> streamable, ObjectKeyFormat objectKeyFormat,
			Structure<? extends Property> structure, List<? extends K> inPrimaryKeys, Object... primaryKeys) {
		super(streamable, objectKeyFormat, structure);
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
