package io.basc.framework.orm;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface ObjectKeyFormat {

	String getObjectKeyByIds(EntityMapping<? extends Property> entityMapping, Elements<?> ids);

	<T> String getObjectKey(EntityMapping<? extends Property> structure, Value source);

	default <K> Map<String, K> getInIdsKeyMap(EntityMapping<? extends Property> structure,
			Elements<? extends K> lastPrimaryKeys, Object[] primaryKey) {
		return getInIdsKeyMap(structure,
				lastPrimaryKeys == null ? Collections.emptyIterator() : lastPrimaryKeys.iterator(), primaryKey);
	}

	default <K> Map<String, K> getInIdsKeyMap(EntityMapping<? extends Property> structure,
			Iterator<? extends K> lastPrimaryKeys, Object[] primaryKeys) {
		if (CollectionUtils.isEmpty(lastPrimaryKeys)) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = new LinkedHashMap<String, K>();
		while (lastPrimaryKeys.hasNext()) {
			K k = lastPrimaryKeys.next();
			Object[] ids;
			if (primaryKeys == null || primaryKeys.length == 0) {
				ids = new Object[] { k };
			} else {
				ids = new Object[primaryKeys.length];
				System.arraycopy(primaryKeys, 0, ids, 0, primaryKeys.length);
				ids[ids.length - 1] = k;
			}
			keyMap.put(getObjectKeyByIds(structure, Elements.forArray(ids)), k);
		}
		return keyMap;
	}
}
