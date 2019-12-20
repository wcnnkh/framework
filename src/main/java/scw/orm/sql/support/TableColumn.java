package scw.orm.sql.support;

import java.lang.reflect.Field;

import scw.core.utils.StringUtils;
import scw.orm.sql.annotation.Column;
import scw.orm.support.FieldColumn;

public class TableColumn extends FieldColumn {
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

	@Override
	public String getDescription() {
		String describe = null;
		Column column = getAnnotation(Column.class);
		if (column != null) {
			describe = column.comment();
		}
		return StringUtils.isEmpty(describe) ? super.getDescription() : describe;
	}
}
