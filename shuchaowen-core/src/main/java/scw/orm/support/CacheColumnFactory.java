package scw.orm.support;

import java.util.HashMap;
import java.util.Map;

import scw.orm.Column;
import scw.orm.ColumnFactory;

public final class CacheColumnFactory implements ColumnFactory {
	private Map<Class<?>, Map<String, ? extends Column>> cacheMap = new HashMap<Class<?>, Map<String, ? extends Column>>();
	private ColumnFactory targetColumnFactory;

	public CacheColumnFactory(ColumnFactory columnFactory) {
		this.targetColumnFactory = columnFactory;
	}

	public Map<String, ? extends Column> getColumnMap(Class<?> clazz) {
		Map<String, ? extends Column> map = cacheMap.get(clazz);
		if (map == null) {
			synchronized (cacheMap) {
				map = cacheMap.get(clazz);
				if (map == null) {
					map = targetColumnFactory.getColumnMap(clazz);
					cacheMap.put(clazz, map);
				}
			}
		}
		return map;
	}
}
