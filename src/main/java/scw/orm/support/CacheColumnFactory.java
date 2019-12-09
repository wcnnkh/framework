package scw.orm.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.orm.AbstractColumnFactory;
import scw.orm.Column;

public class CacheColumnFactory extends AbstractColumnFactory {
	private volatile Map<Class<?>, Map<String, Column>> fieldDefinitionMap = new HashMap<Class<?>, Map<String, Column>>();

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		Map<String, Column> map = fieldDefinitionMap.get(clazz);
		if (map == null) {
			synchronized (fieldDefinitionMap) {
				map = fieldDefinitionMap.get(clazz);
				if (map == null) {
					map = Collections.unmodifiableMap(analysisClass(clazz));
					fieldDefinitionMap.put(clazz, map);
				}
			}
		}
		return map;
	}
}
