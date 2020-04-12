package scw.orm.support;

import java.util.Map;

import scw.orm.Column;
import scw.orm.ColumnFactory;
import scw.util.ConcurrentReferenceHashMap;

public class CacheColumnFactoryWrapper implements ColumnFactory {
	private Map<Class<?>, Map<String, ? extends Column>> cacheMap = new ConcurrentReferenceHashMap<Class<?>, Map<String, ? extends Column>>();
	private ColumnFactory targetColumnFactory;
	
	public CacheColumnFactoryWrapper(ColumnFactory columnFactory) {
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
