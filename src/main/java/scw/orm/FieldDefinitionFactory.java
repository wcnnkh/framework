package scw.orm;

import java.util.Map;

import scw.core.reflect.FieldDefinition;

public interface FieldDefinitionFactory {
	Map<String, FieldDefinition> getFieldDefinitionMap(Class<?> clazz);

	FieldDefinition getFieldDefinition(Class<?> clazz, String name);
}
