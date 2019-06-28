package scw.sql.orm;

import java.lang.reflect.Field;

import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Counter;

public final class DefaultColumnInfo implements ColumnInfo {
	private final String name;// 数据库字段名
	private final boolean primaryKey;// 索引
	private final boolean autoIncrement;
	private final boolean isDataBaseType;
	private final Field field;
	private final Counter counter;

	protected DefaultColumnInfo(String defaultTableName, Field field) {
		this.counter = field.getAnnotation(Counter.class);
		this.autoIncrement = field.getAnnotation(AutoIncrement.class) != null;
		this.field = field;
		this.name = ORMUtils.getAnnotationColumnName(field);
		this.primaryKey = ORMUtils.isAnnoataionPrimaryKey(field);
		Class<?> type = field.getType();
		this.isDataBaseType = ORMUtils.isDataBaseType(type);
	}

	private Object fieldValueToDBValue(Object value) {
		if (boolean.class == field.getType()) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == field.getType()) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}
		return value;
	}

	public Object getValueToDB(Object bean) throws IllegalArgumentException, IllegalAccessException {
		return fieldValueToDBValue(field.get(bean));
	}

	public void setValueToField(Object bean, Object dbValue) throws IllegalArgumentException, IllegalAccessException {
		field.set(bean, ORMUtils.parse(field.getType(), dbValue));
	}

	public String getName() {
		return name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public String getTypeName() {
		return ORMUtils.getAnnotationColumnTypeName(field);
	}

	public Class<?> getType() {
		return field.getType();
	}

	public int getLength() {
		return ORMUtils.getAnnotationColumnLength(field);
	}

	public boolean isNullAble() {
		return ORMUtils.isAnnoataionColumnNullAble(field);
	}

	/**
	 * 把指定的表名和字段组合在一起
	 * 
	 * @param tableName
	 * @return
	 */
	public String getSQLName(String tableName) {
		StringBuilder sb = new StringBuilder(32);
		if (tableName != null && tableName.length() != 0) {
			sb.append("`");
			sb.append(tableName);
			sb.append("`.");
		}
		sb.append("`").append(name).append("`");
		return sb.toString();
	}

	public boolean isDataBaseType() {
		return isDataBaseType;
	}

	public Field getField() {
		return field;
	}

	public boolean isUnique() {
		return ORMUtils.isAnnoataionColumnUnique(field);
	}

	public Counter getCounter() {
		return counter;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}
}
