package io.basc.framework.orm;

import java.util.Map;

import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Pagination;

public class PrimaryKeyQuery<K, V> extends EntityQuery<V> {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends K> inPrimaryKeys;
	private final Object[] primaryKeys;

	public PrimaryKeyQuery(Pagination<V> pagination, EntityMapping<? extends Property> entityMapping,
			ObjectKeyFormat objectKeyFormat, Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		super(pagination, entityMapping, objectKeyFormat);
		this.inPrimaryKeys = inPrimaryKeys;
		this.primaryKeys = primaryKeys;
	}

	public PrimaryKeyQuery(Elements<V> elements, EntityMapping<? extends Property> entityMapping,
			ObjectKeyFormat objectKeyFormat, Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		super(elements, entityMapping, objectKeyFormat);
		this.inPrimaryKeys = inPrimaryKeys;
		this.primaryKeys = primaryKeys;
	}

	public Elements<? extends K> getInPrimaryKeys() {
		return inPrimaryKeys;
	}

	public Object[] getPrimaryKeys() {
		return primaryKeys;
	}

	public Map<K, V> toMap() {
		return toMap(inPrimaryKeys, primaryKeys);
	}
}
