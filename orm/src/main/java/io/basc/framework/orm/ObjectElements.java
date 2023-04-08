package io.basc.framework.orm;

import java.util.List;
import java.util.Map;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.StreamElements;
import io.basc.framework.util.Streamable;

public class ObjectElements<E> extends StreamElements<E> {
	private final ObjectKeyFormat objectKeyFormat;
	private final Structure<? extends Property> structure;

	public ObjectElements(Streamable<E> streamable, ObjectKeyFormat objectKeyFormat,
			Structure<? extends Property> structure) {
		super(streamable);
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
