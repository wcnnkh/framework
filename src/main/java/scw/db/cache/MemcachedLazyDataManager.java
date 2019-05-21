package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.data.memcached.Memcached;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class MemcachedLazyDataManager extends LazyDataManager {
	private final Memcached memcached;

	public MemcachedLazyDataManager(int exp, Memcached memcached) {
		super(exp);
		this.memcached = memcached;
	}

	public boolean save(Object bean) {
		return memcached.add(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
	}

	public boolean update(Object bean) {
		return memcached.set(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
	}

	public boolean delete(Object bean) {
		memcached.delete(getObjectKey(ORMUtils.getTableInfo(bean.getClass()),
				bean));
		return true;
	}

	public boolean deleteById(Class<?> type, Object... params) {
		memcached.delete(getObjectKeyById(type, params));
		return true;
	}

	public boolean saveOrUpdate(Object bean) {
		memcached.set(
				getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean),
				getExp(), bean);
		return true;
	}

	public <T> T getById(Class<T> type, Object... params) {
		return memcached.getAndTouch(getObjectKeyById(type, params), getExp());
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
		Map<String, K> keyMap = new HashMap<String, K>(inIds.size(), 1);
		for (K k : inIds) {
			keyMap.put(appendObjectKey(key, k), k);
		}

		Map<String, V> map = memcached.get(keyMap.keySet());
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
