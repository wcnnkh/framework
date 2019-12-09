package scw.orm.sql;

import java.lang.reflect.Field;

import scw.core.utils.StringUtils;
import scw.orm.sql.annotation.Column;
import scw.orm.support.DefaultColumn;

public class TableColumn extends DefaultColumn {
	private String name;

	public TableColumn(Class<?> clazz, Field field) {
		super(clazz, field);

		Column column = getAnnotation(Column.class);
		if (column != null && !StringUtils.isEmpty(column.name())) {
			this.name = column.name();
		}
	}

	public String getName() {
		return name == null ? super.getName() : name;
	}
}
