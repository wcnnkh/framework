package scw.db.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class FullCacheManager implements CacheManager {
	private static final String PREFIX = "full:";

	abstract void add(String key, Object value);

	abstract void set(String key, Object value);

	abstract void delete(String key);

	abstract <T> T get(Class<T> type, String key);

	abstract <T> Map<String, T> get(Class<T> type, Collection<String> keys);

	abstract Map<String, String> getMap(String key);

	abstract void mapAdd(String key, String field, String value);

	abstract void mapRemove(String key, String field);

	private String getObjectKey(TableInfo tableInfo, Object bean) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (ColumnInfo c : tableInfo.getPrimaryKeyColumns()) {
			sb.append("&");
			sb.append(c.getField().get(bean));
		}
		return sb.toString();
	}

	private String getObjectKeyById(TableInfo tableInfo, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}
		return sb.toString();
	}

	private void savefullKeys(TableInfo tableInfo, String objectKey, Object[] primaryKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < primaryKeys.length; i++) {
			if (i == primaryKeys.length - 1) {
				continue;
			}

			String v = primaryKeys[i].toString();
			mapAdd(sb.toString(), v, objectKey);

			sb.append("&");
			sb.append(v);
		}
	}

	private void removeFullKeys(TableInfo tableInfo, Object[] primaryKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < primaryKeys.length; i++) {
			if (i == primaryKeys.length - 1) {
				continue;
			}

			String v = primaryKeys[i].toString();
			mapRemove(sb.toString(), v);
			sb.append("&");
			sb.append(v);
		}
	}

	public void save(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args;
		try {
			args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String objectKey = getObjectKeyById(tableInfo, args);
		add(objectKey, bean);
		savefullKeys(tableInfo, objectKey, args);
	}

	public void update(Object bean) {
		String objectKey;
		try {
			objectKey = getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		set(objectKey, bean);
	}

	public void delete(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args;
		try {
			args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String objectKey = getObjectKeyById(tableInfo, args);
		delete(objectKey);
		removeFullKeys(tableInfo, args);
	}

	public void deleteById(Class<?> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String objectKey = getObjectKeyById(tableInfo, params);
		delete(objectKey);
		removeFullKeys(tableInfo, params);
	}

	public void saveOrUpdate(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args;
		try {
			args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String objectKey = getObjectKeyById(tableInfo, args);
		set(objectKey, bean);
		savefullKeys(tableInfo, objectKey, args);
	}

	public <T> T getById(Class<T> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String objectKey = getObjectKeyById(tableInfo, params);
		return get(type, objectKey);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String key = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = getMap(key);
		if (keyMap == null) {
			return null;
		}

		Map<String, T> valueMap = get(type, keyMap.values());
		if (valueMap == null || valueMap.isEmpty()) {
			return null;
		}

		List<T> valueList = new ArrayList<T>();
		for (Entry<String, String> entry : keyMap.entrySet()) {
			valueList.add(valueMap.get(entry.getValue()));
		}
		return valueList;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String indexKey = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = getMap(indexKey);
		if (keyMap == null) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			map.put(objectKey, key);
		}

		if (map.isEmpty()) {
			return null;
		}

		Map<String, V> valueMap = get(type, map.keySet());
		Map<K, V> result = new HashMap<K, V>(valueMap.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			if (objectKey == null) {
				continue;
			}

			V v = valueMap.get(objectKey);
			if (v == null) {
				continue;
			}

			result.put(k, v);
		}
		return result;
	}
}
