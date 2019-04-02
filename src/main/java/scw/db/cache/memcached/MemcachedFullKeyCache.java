package scw.db.cache.memcached;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.exception.ParameterException;
import scw.db.cache.Cache;
import scw.memcached.CAS;
import scw.memcached.Memcached;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

/**
 * 缓存所有的key
 * 
 * @author shuchaowen
 *
 */
public final class MemcachedFullKeyCache implements Cache {
	private final Memcached memcached;
	private final int exp;

	public MemcachedFullKeyCache(Memcached memcached, int exp) {
		this.memcached = memcached;
		this.exp = exp;
	}

	private boolean casAdd(String key, String value, String objectKey) {
		CAS<LinkedHashMap<String, String>> cas = memcached.gets(key);
		if (cas == null) {
			LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
			valueMap.put(value, objectKey);
			return memcached.cas(key, valueMap, 0);
		} else {
			LinkedHashMap<String, String> valueMap = cas.getValue();
			valueMap.put(value, objectKey);
			return memcached.cas(key, valueMap, cas.getCas());
		}
	}

	private boolean casRemove(String key, String value) {
		CAS<LinkedHashMap<String, String>> cas = memcached.gets(key);
		if (cas == null) {
			return true;
		}

		LinkedHashMap<String, String> valueMap = cas.getValue();
		valueMap.remove(value);
		return memcached.cas(key, valueMap, cas.getCas());
	}

	private String getObjectKey(TableInfo tableInfo, Object bean)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (ColumnInfo c : tableInfo.getPrimaryKeyColumns()) {
			sb.append("&");
			sb.append(c.getFieldInfo().forceGet(bean));
		}
		return sb.toString();
	}

	private String getObjectKeyById(TableInfo tableInfo, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}
		return sb.toString();
	}

	public void save(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		memcached.add(objectKey, exp, bean);
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			sb.append("&");
			sb.append(args[i]);
			if (i > 0 && i < args.length - 1) {
				String indexKey = sb.toString();
				while (casAdd(indexKey, args[i].toString(), objectKey)) {
					break;
				}
			}
		}
	}

	public void update(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String objectKey = getObjectKey(tableInfo, bean);
		memcached.set(objectKey, bean);
	}

	public void delete(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		memcached.add(objectKey, exp, bean);
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			sb.append("&");
			sb.append(args[i]);
			if (i > 0 && i < args.length - 1) {
				String indexKey = sb.toString();
				while (casRemove(indexKey, objectKey)) {
					break;
				}
			}
		}
	}

	public void saveOrUpdate(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		memcached.set(objectKey, exp, bean);
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			sb.append("&");
			sb.append(args[i]);
			if (i > 0 && i < args.length - 1) {
				String indexKey = sb.toString();
				while (casAdd(indexKey, args[i].toString(), objectKey)) {
					break;
				}
			}
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		StringBuilder sb = new StringBuilder();
		sb.append("fullKeys:").append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}

		T t = memcached.get(sb.toString());
		return ORMUtils.restartFieldLinsten(t);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		if (params == null || params.length == 0) {
			throw new ParameterException("此缓存方式至少需要一个以上的主键支持");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length <= 1) {
			throw new ParameterException("主键数量错误，至少要两个主键");
		}

		if (tableInfo.getPrimaryKeyColumns().length == params.length) {
			T t = getById(type, params);
			if (t == null) {
				return null;
			}

			return Arrays.asList(t);
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new ParameterException("主键参数错误");
		}

		String key = getObjectKeyById(tableInfo, params);
		LinkedHashMap<String, String> keyMap = memcached.get(key);
		if (keyMap == null) {
			return null;
		}

		Map<String, T> valueMap = memcached.get(keyMap.values());
		if (valueMap == null || valueMap.isEmpty()) {
			return null;
		}

		List<T> valueList = new ArrayList<T>();
		for (Entry<String, String> entry : keyMap.entrySet()) {
			valueList.add(valueMap.get(entry.getValue()));
		}
		return valueList;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds,
			Object... params) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length <= 1) {
			throw new ParameterException("主键数量错误，至少要两个主键");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new ParameterException("主键参数错误");
		}

		String indexKey = getObjectKeyById(tableInfo, params);
		LinkedHashMap<String, String> keyMap = memcached.get(indexKey);
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

		Map<String, V> valueMap = memcached.get(map.keySet());
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
