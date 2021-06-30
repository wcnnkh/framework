package scw.orm;

import java.util.Collection;
import java.util.Map;

import scw.env.Sys;

public interface ObjectKeyFormat {
	/**
	 * 默认对象主键的连接符
	 */
	static final String OBJECT_KEY_CONNECTOR = Sys.env.getValue("object.key.connector.character", String.class, ":");

	String getObjectKeyByIds(Class<?> clazz, Collection<Object> ids);

	<T> String getObjectKey(Class<? extends T> clazz, T bean);

	<K> Map<String, K> getInIdsKeyMap(Class<?> clazz, Collection<? extends K> lastPrimaryKeys, Object[] primaryKeys);
}
