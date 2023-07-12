package io.basc.framework.orm;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.util.CollectionUtils;

public interface EntityKeyGenerator {

	default <T> String getEntityKey(EntityRepository<? extends T> repository, T entity) {
		List<? extends Property> elements = repository.getEntityMapping().getPrimaryKeys().toList();
		List<Object> values = elements.stream().map((e) -> e.getGetters().first().get(entity))
				.collect(Collectors.toList());
		return getEntityKey(repository, elements.iterator(), values.iterator());
	}

	String getEntityKey(EntityRepository<?> repository, Iterator<? extends Property> propertyIterator,
			Iterator<? extends Object> valueIterator);

	default <K> Map<String, K> getEntityKeyMap(EntityRepository<?> repository, Iterator<? extends K> lastPrimaryKeys,
			Object[] primaryKeys) {
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

			String key = getEntityKey(repository, repository.getEntityMapping().getPrimaryKeys().iterator(),
					Arrays.asList(ids).iterator());
			keyMap.put(key, k);
		}
		return keyMap;
	}
}
