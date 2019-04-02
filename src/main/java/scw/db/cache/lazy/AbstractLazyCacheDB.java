package scw.db.cache.lazy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.db.DB;
import scw.db.cache.CacheUtils;
import scw.db.cache.annotation.Cache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class AbstractLazyCacheDB extends DB {
	protected abstract <T> T get(Class<T> type, String key);

	protected abstract void delete(String key);

	protected abstract void add(String key, Object bean);

	protected abstract void set(String key, Object bean);

	protected abstract <T> Map<String, T> getMap(Class<T> type, Collection<String> keys);

	protected boolean cacheEnable(Class<?> clz) {
		TableInfo tableInfo = ORMUtils.getTableInfo(clz);
		Cache cache = tableInfo.getClassInfo().getClz().getAnnotation(Cache.class);
		return cache != null;
	}

	public void deleteCache(Class<?> clz, Object... params) {
		if (cacheEnable(clz)) {
			delete(CacheUtils.getByIdCacheKey(clz, params));
		}
	}

	public void deleteCache(Object bean) {
		if (cacheEnable(bean.getClass())) {
			delete(CacheUtils.getObjectCacheKey(bean));
		}
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (!cacheEnable(type)) {
			return super.getById(tableName, type, params);
		}

		String cacheKey = CacheUtils.getByIdCacheKey(type, params);
		T t = get(type, cacheKey);
		if (t == null) {
			t = super.getById(tableName, type, params);
			if (t != null) {
				set(cacheKey, t);
			}
		}
		return t;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b) {
			deleteCache(bean);
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b) {
			deleteCache(type, params);
		}
		return b;
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b) {
			if (cacheEnable(bean.getClass())) {
				add(CacheUtils.getObjectCacheKey(bean), bean);
			}
		}
		return b;
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b) {
			deleteCache(bean);
		}
		return b;
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b) {
			deleteCache(bean);
		}
		return b;
	}

	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (!cacheEnable(type)) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length == tableInfo.getPrimaryKeyColumns().length - 1) {
			Map<String, K> keyMap = new HashMap<String, K>();
			Iterator<K> iterator = inIds.iterator();
			String id = CacheUtils.getByIdCacheKey(type, params);
			while (iterator.hasNext()) {
				K k = iterator.next();
				keyMap.put(id + k.toString(), k);
			}

			Map<String, V> map = getMap(type, keyMap.keySet());
			if (map == null || map.isEmpty()) {
				return super.getInIdList(type, tableName, inIds, params);
			}

			Map<K, V> valueMap = new HashMap<K, V>();
			for (Entry<String, V> entry : map.entrySet()) {
				valueMap.put(keyMap.get(entry.getKey()), entry.getValue());
			}

			if (valueMap.size() == inIds.size()) {
				return valueMap;
			}

			LinkedList<K> notCacheMap = new LinkedList<K>();
			iterator = inIds.iterator();
			while (iterator.hasNext()) {
				K k = iterator.next();
				if (!map.containsKey(k)) {
					notCacheMap.add(k);
				}
			}

			Map<K, V> dbMap = super.getInIdList(type, tableName, inIds, params);
			if (dbMap != null) {
				for (Entry<K, V> entry : dbMap.entrySet()) {
					V bean = entry.getValue();
					if (bean == null) {
						continue;
					}
					set(CacheUtils.getObjectCacheKey(bean), bean);
					valueMap.put(entry.getKey(), bean);
				}
			}
			return valueMap;
		} else {
			return super.getInIdList(type, tableName, inIds, params);
		}
	}
}
