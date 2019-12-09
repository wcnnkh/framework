package scw.orm.support;

import java.lang.reflect.Field;

import scw.core.utils.StringUtils;
import scw.orm.FieldColumn;

public class DefaultColumn extends FieldColumn {
	private String name;

	public DefaultColumn(Class<?> clazz, Field field) {
		super(clazz, field);
		scw.orm.annotation.ColumnName columnName = getAnnotation(scw.orm.annotation.ColumnName.class);
		if (columnName != null && !StringUtils.isEmpty(columnName.value())) {
			this.name = columnName.value();
		}
	}

	public String getName() {
		return name == null ? super.getName() : name;
	}
}
