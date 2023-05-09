package io.basc.framework.orm;

import java.util.Map;

import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Pagination;
import io.basc.framework.value.Value;

public class EntityQuery<E> extends Query<E> {
	private static final long serialVersionUID = 1L;
	private final ObjectKeyFormat objectKeyFormat;
	private final EntityMapping<? extends Property> entityMapping;

	public EntityQuery(Pagination<E> pagination, EntityMapping<? extends Property> entityMapping,
			ObjectKeyFormat objectKeyFormat) {
		super(pagination);
		this.objectKeyFormat = objectKeyFormat;
		this.entityMapping = entityMapping;
	}

	public EntityQuery(Elements<E> elements, EntityMapping<? extends Property> entityMapping,
			ObjectKeyFormat objectKeyFormat) {
		super(elements);
		this.entityMapping = entityMapping;
		this.objectKeyFormat = objectKeyFormat;
	}

	public ObjectKeyFormat getObjectKeyFormat() {
		return objectKeyFormat;
	}

	public EntityMapping<? extends Property> getEntityMapping() {
		return entityMapping;
	}

	public <K> Map<K, E> toMap(Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		Map<String, K> keyMap = objectKeyFormat.getInIdsKeyMap(entityMapping, inPrimaryKeys, primaryKeys);
		return getElements().toMap((e) -> {
			String key = objectKeyFormat.getObjectKey(entityMapping, Value.of(e));
			return keyMap.get(key);
		});
	}
}
