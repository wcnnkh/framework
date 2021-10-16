package io.basc.framework.orm;

import io.basc.framework.env.Sys;

import java.util.Collection;
import java.util.Map;

public interface ObjectKeyFormat {
	/**
	 * 默认对象主键的连接符
	 */
	static final String OBJECT_KEY_CONNECTOR = Sys.env.getValue("object.key.connector.character", String.class, ":");

	String getObjectKeyByIds(EntityStructure<?> structure, Collection<Object> ids);

	<T> String getObjectKey(EntityStructure<?> structure, T bean);

	<K> Map<String, K> getInIdsKeyMap(EntityStructure<?> structure, Collection<? extends K> lastPrimaryKeys, Object[] primaryKeys);
}
