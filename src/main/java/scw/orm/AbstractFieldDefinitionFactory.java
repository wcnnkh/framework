package scw.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;

import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;

public abstract class AbstractFieldDefinitionFactory implements FieldDefinitionFactory {

	protected FieldDefinition analysisField(Class<?> clazz, Field field) {
		return new DefaultFieldDefinition(clazz, field, false, false);
	}

	protected LinkedHashMap<String, FieldDefinition> analysisClass(Class<?> clazz) {
		LinkedHashMap<String, FieldDefinition> map = new LinkedHashMap<String, FieldDefinition>();
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			ReflectUtils.setAccessibleField(field);
			FieldDefinition fieldDefinition = analysisField(clazz, field);
			if (map.containsKey(fieldDefinition.getName())) {
				throw new AlreadyExistsException("Class " + clazz.getName() + " field " + fieldDefinition.getName());
			}
			map.put(fieldDefinition.getName(), fieldDefinition);
		}
		return map;
	}
}
