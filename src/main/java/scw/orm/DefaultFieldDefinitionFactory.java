package scw.orm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.core.reflect.FieldDefinition;

public class DefaultFieldDefinitionFactory extends AbstractFieldDefinitionFactory {
	private volatile Map<Class<?>, Map<String, FieldDefinition>> fieldDefinitionMap = new HashMap<Class<?>, Map<String, FieldDefinition>>();

	public Map<String, FieldDefinition> getFieldDefinitionMap(Class<?> clazz) {
		Map<String, FieldDefinition> map = fieldDefinitionMap.get(clazz);
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
