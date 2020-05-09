package scw.orm.sql.support;

import java.lang.reflect.Field;

import scw.core.utils.StringUtils;
import scw.orm.support.DefaultFieldColumn;
import scw.sql.orm.annotation.Column;

public class TableFieldColumn extends DefaultFieldColumn {
	private String name;

	public TableFieldColumn(Class<?> clazz, Field field) {
		super(clazz, field, false, false);
		Column column = getAnnotatedElement().getAnnotation(Column.class);
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
		Column column = getAnnotatedElement().getAnnotation(Column.class);
		if (column != null) {
			describe = column.comment();
		}
		return StringUtils.isEmpty(describe) ? super.getDescription() : describe;
	}
}
