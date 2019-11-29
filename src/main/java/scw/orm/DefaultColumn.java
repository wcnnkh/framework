package scw.orm;

import java.lang.reflect.Field;

import scw.orm.annotation.ColumnName;

public class DefaultColumn extends AbstractColumn {
	private final String name;

	public DefaultColumn(Class<?> clazz, Field field) {
		super(clazz, field, true, true);
		ColumnName columnName = getAnnotation(ColumnName.class);
		this.name = columnName == null ? field.getName() : columnName.value();
	}

	public String getName() {
		return name;
	}
}
