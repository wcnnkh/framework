package scw.orm;

import java.lang.reflect.Field;

import scw.orm.annotation.ColumnName;

public final class DefaultFieldDefinition extends AbstractFieldDefinition {
	private final String name;

	public DefaultFieldDefinition(Class<?> clazz, Field field, boolean getter, boolean setter) {
		super(clazz, field, getter, setter);
		ColumnName columnName = getAnnotation(ColumnName.class);
		this.name = columnName == null ? field.getName() : columnName.value();
	}

	public String getName() {
		return name;
	}
}
