package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.data.redis.Redis;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class RedisLazyDataManager extends LazyDataManager {
	private final Redis redis;

	public RedisLazyDataManager(int exp, Redis redis) {
		super(exp);
		this.redis = redis;
	}

	public boolean save(Object bean) {
		redis.getObjectOperations().setex(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
		return true;
	}

	public boolean update(Object bean) {
		redis.getObjectOperations().setex(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
		return true;
	}

	public boolean delete(Object bean) {
		redis.getStringOperations().del(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean));
		return true;
	}

	public boolean deleteById(Class<?> type, Object... params) {
		redis.getStringOperations().del(getObjectKeyById(type, params));
		return true;
	}

	public boolean saveOrUpdate(Object bean) {
		redis.getObjectOperations().setex(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T getById(Class<T> type, Object... params) {
		return (T) redis.getObjectOperations().getAndTouch(
				getObjectKeyById(type, params), getExp());
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds,
			Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (params.length != tableInfo.getPrimaryKeyColumns().length - 1) {
			return null;
		}

		String key = getObjectKeyById(type, params);
		Map<String, K> keyMap = new LinkedHashMap<String, K>(inIds.size(), 1);
		for (K k : inIds) {
			keyMap.put(appendObjectKey(key, k), k);
		}

		@SuppressWarnings("unchecked")
		Map<String, V> map = (Map<String, V>) redis.getObjectOperations().mget(
				keyMap.keySet());
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<K, V> valueMap = new HashMap<K, V>(map.size(), 1);
		for (Entry<String, V> entry : map.entrySet()) {
			K k = keyMap.get(entry.getKey());
			if (k == null) {
				continue;
			}

			valueMap.put(k, entry.getValue());
		}
		return valueMap;
	}

}
