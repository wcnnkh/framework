package scw.sql.orm.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.sql.orm.AbstractORMTemplate;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;

public abstract class AbstractORMCacheTemplate extends AbstractORMTemplate {
	private final Cache cache;

	public AbstractORMCacheTemplate(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat);
		this.cache = cache;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (cache == null) {
			return super.getById(tableName, type, params);
		}

		String cacheKey = CacheUtils.getByIdCacheKey(type, params);
		T t = cache.get(type, cacheKey);
		if (t == null) {
			t = super.getById(tableName, type, params);
			if (t != null) {
				cache.set(cacheKey, t);
			}
		}
		return t;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		if (cache == null) {
			return super.delete(bean, tableName);
		}

		cache.delete(CacheUtils.getObjectCacheKey(bean));
		return super.delete(bean, tableName);
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		if (cache == null) {
			return super.deleteById(tableName, type, params);
		}

		cache.delete(CacheUtils.getByIdCacheKey(type, params));
		return super.deleteById(tableName, type, params);
	}

	@Override
	public boolean save(Object bean, String tableName) {
		if (cache == null) {
			return super.save(bean, tableName);
		}

		cache.add(CacheUtils.getObjectCacheKey(bean), bean);
		return super.save(bean, tableName);
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		if (cache == null) {
			return super.saveOrUpdate(bean, tableName);
		}

		cache.delete(CacheUtils.getObjectCacheKey(bean));
		return super.saveOrUpdate(bean, tableName);
	}

	@Override
	public boolean update(Object bean, String tableName) {
		if (cache == null) {
			return super.update(bean, tableName);
		}

		cache.delete(CacheUtils.getObjectCacheKey(bean));
		return super.update(bean, tableName);
	}

	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (cache == null) {
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

			Map<String, V> map = cache.getMap(type, keyMap.keySet());
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
					cache.set(CacheUtils.getObjectCacheKey(bean), bean);
					valueMap.put(entry.getKey(), bean);
				}
			}
			return valueMap;
		} else {
			return super.getInIdList(type, tableName, inIds, params);
		}
	}
}
