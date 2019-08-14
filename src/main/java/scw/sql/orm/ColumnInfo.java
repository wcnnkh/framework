package scw.sql.orm;

import java.lang.reflect.Field;

import scw.sql.orm.annotation.Counter;
import scw.sql.orm.enums.CasType;

public interface ColumnInfo {
	String getName();

	boolean isPrimaryKey();

	String getTypeName();

	Class<?> getType();

	int getLength();

	boolean isNullAble();

	boolean isDataBaseType();

	Field getField();

	boolean isUnique();

	Counter getCounter();

	boolean isAutoIncrement();
	
	CasType getCasType();
	
	Object get(Object bean) throws Exception;
	
	void set(Object bean, Object value) throws Exception;
}
