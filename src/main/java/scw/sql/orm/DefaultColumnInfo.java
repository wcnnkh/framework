package scw.sql.orm;

import java.lang.reflect.Field;

public final class DefaultColumnInfo extends AbstractColumnInfo {
	private final ColumnConvert convert;

	protected DefaultColumnInfo(Field field) {
		super(field);
		this.convert = ORMUtils.getConvert(field);
	}

	public boolean isDataBaseType() {
		return ORMUtils.isDataBaseField(getField());
	}

	public Object get(Object bean) throws Exception {
		return convert.getter(getField(), bean);
	}

	public void set(Object bean, Object value) throws Exception {
		convert.setter(getField(), bean, value);
	}

	public Object toSqlField(Object value) throws Exception {
		return convert.toSqlField(getField(), value);
	}
}
