package io.basc.framework.orm;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.StandardResultSet;

public class ObjectResultSet<E> extends StandardResultSet<E> {
	private final ObjectKeyFormat objectKeyFormat;
	private final Structure<? extends Property> structure;

	public ObjectResultSet(Supplier<? extends Cursor<E>> cursorSupplier, ObjectKeyFormat objectKeyFormat,
			Structure<? extends Property> structure) {
		super(cursorSupplier);
		this.objectKeyFormat = objectKeyFormat;
		this.structure = structure;
	}

	public ObjectKeyFormat getObjectKeyFormat() {
		return objectKeyFormat;
	}

	public Structure<? extends Property> getStructure() {
		return structure;
	}

	public <K> Map<K, E> toMap(List<? extends K> inPrimaryKeys, Object... primaryKeys) {
		Map<String, K> keyMap = objectKeyFormat.getInIdsKeyMap(structure, inPrimaryKeys, primaryKeys);
		return toMap((e) -> {
			String key = objectKeyFormat.getObjectKey(structure, e);
			return keyMap.get(key);
		});
	}
}
