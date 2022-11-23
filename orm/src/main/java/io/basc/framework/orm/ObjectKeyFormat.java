package io.basc.framework.orm;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.CollectionUtils;

public interface ObjectKeyFormat {
	String getObjectKeyByIds(Structure<? extends Property> structure, Iterator<?> ids);

	default String getObjectKeyByIds(Structure<? extends Property> structure, Iterable<?> ids) {
		return getObjectKeyByIds(structure, ids == null ? Collections.emptyIterator() : ids.iterator());
	}

	<T> String getObjectKey(Structure<? extends Property> structure, T bean);

	default <K> Map<String, K> getInIdsKeyMap(Structure<? extends Property> structure,
			Iterable<? extends K> lastPrimaryKeys, Object[] primaryKey) {
		return getInIdsKeyMap(structure,
				lastPrimaryKeys == null ? Collections.emptyIterator() : lastPrimaryKeys.iterator(), primaryKey);
	}

	default <K> Map<String, K> getInIdsKeyMap(Structure<? extends Property> structure,
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
			keyMap.put(getObjectKeyByIds(structure, Arrays.asList(ids)), k);
		}
		return keyMap;
	}
}
