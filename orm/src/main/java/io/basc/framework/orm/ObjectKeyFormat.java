package io.basc.framework.orm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.mapper.Structure;
import io.basc.framework.util.CollectionUtils;

public interface ObjectKeyFormat {
	String getObjectKeyByIds(Structure<? extends Property> structure, Collection<Object> ids);

	<T> String getObjectKey(Structure<? extends Property> structure, T bean);

	default <K> Map<String, K> getInIdsKeyMap(Structure<? extends Property> structure,
			Collection<? extends K> lastPrimaryKeys, Object[] primaryKeys) {
		if (CollectionUtils.isEmpty(lastPrimaryKeys)) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = new LinkedHashMap<String, K>();
		Iterator<? extends K> valueIterator = lastPrimaryKeys.iterator();

		while (valueIterator.hasNext()) {
			K k = valueIterator.next();
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
