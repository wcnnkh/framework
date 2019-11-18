package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.data.Cache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class AbstractCacheManager<C extends Cache> implements CacheManager {
	public abstract C getCache();

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (params.length != tableInfo.getPrimaryKeyColumns().length - 1) {
			return null;
		}

		String key = ORMUtils.getObjectKeyById(type, params);
		Map<String, K> keyMap = new HashMap<String, K>(inIds.size(), 1);
		for (K k : inIds) {
			keyMap.put(appendObjectKey(key, k), k);
		}

		Map<String, V> map = getCache().get(keyMap.keySet());
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

		for (Entry<String, K> entry : keyMap.entrySet()) {
			if (valueMap.containsKey(entry.getValue())) {
				continue;
			}

			if (getCache().isExist(entry.getKey())) {
				valueMap.put(entry.getValue(), null);
			}
		}
		return valueMap;
	}

	protected final String appendObjectKey(String key, Object value) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		ORMUtils.appendObjectKey(sb, value);
		return sb.toString();
	}

	public boolean isSearchDB(Class<?> type, Object... params) {
		return true;
	}
}
