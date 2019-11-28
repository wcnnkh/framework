package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.data.Cache;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableMappingContext;
import scw.orm.sql.support.SqlORMUtils;

public abstract class AbstractCacheManager<C extends Cache> implements CacheManager {
	public abstract C getCache();

	public SqlMapper getSqlMapper() {
		return SqlORMUtils.getSqlMapper();
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		TableMappingContext tableFieldContext = getSqlMapper().getTableMappingContext(type);

		if (params.length != tableFieldContext.getPrimaryKeys().size() - 1) {
			return null;
		}

		Map<String, K> keyMap = getSqlMapper().getInIdKeyMap(type, inIds, params);
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

	public boolean isSearchDB(Class<?> type, Object... params) {
		return true;
	}
}
