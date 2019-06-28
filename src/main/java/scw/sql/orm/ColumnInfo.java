package scw.sql.orm;

import java.lang.reflect.Field;

import scw.sql.orm.annotation.Counter;

public interface ColumnInfo {
	Object getValueToDB(Object bean) throws IllegalArgumentException, IllegalAccessException;

	void setValueToField(Object bean, Object dbValue) throws IllegalArgumentException, IllegalAccessException;

	String getName();

	boolean isPrimaryKey();

	String getTypeName();

	Class<?> getType();

	int getLength();

	boolean isNullAble();

	/**
	 * 把指定的表名和字段组合在一起
	 * 
	 * @param tableName
	 * @return
	 */
	String getSQLName(String tableName);

	boolean isDataBaseType();

	Field getField();

	boolean isUnique();

	Counter getCounter();

	boolean isAutoIncrement();
}
