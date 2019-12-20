package scw.orm.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.reflect.ReflectionUtils;
import scw.lang.AlreadyExistsException;
import scw.orm.Column;
import scw.orm.ColumnFactory;

public abstract class FieldColumnFactory implements ColumnFactory {

	protected Column analysisField(Class<?> clazz, Field field) {
		return new FieldColumn(clazz, field);
	}

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		LinkedHashMap<String, Column> map = new LinkedHashMap<String, Column>();
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			ReflectionUtils.setAccessibleField(field);
			Column column = analysisField(clazz, field);
			if (map.containsKey(column.getName())) {
				throw new AlreadyExistsException("Class " + clazz.getName() + " column " + column.getName());
			}
			map.put(column.getName(), column);
		}
		return map;
	}
}
