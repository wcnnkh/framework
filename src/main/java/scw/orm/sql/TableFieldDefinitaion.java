package scw.orm.sql;

import java.lang.reflect.Field;

import scw.core.utils.StringUtils;
import scw.orm.AbstractFieldDefinition;
import scw.orm.annotation.ColumnName;
import scw.orm.sql.annotation.Column;

public class TableFieldDefinitaion extends AbstractFieldDefinition {
	private String name;

	public TableFieldDefinitaion(Class<?> clazz, Field field) {
		super(clazz, field, false, false);
		Column column = getAnnotation(Column.class);
		if (column != null) {
			this.name = column.name();
		}

		ColumnName columnName = getAnnotation(ColumnName.class);
		if (columnName != null) {
			this.name = column.name();
		}
		this.name = StringUtils.isEmpty(name) ? null : name;
	}

	public String getName() {
		return name == null ? getField().getName() : name;
	}
}
