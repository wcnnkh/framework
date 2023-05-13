package io.basc.framework.orm;

import java.util.Map;

import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;

public class PrimaryKeyQuery<K, V> extends EntityQuery<V> {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends K> inPrimaryKeys;
	private final Object[] primaryKeys;

	public PrimaryKeyQuery(Query<V> packagedQuery, EntityMapping<? extends Property> entityMapping,
			ObjectKeyFormat objectKeyFormat, Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		super(packagedQuery, entityMapping, objectKeyFormat);
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
