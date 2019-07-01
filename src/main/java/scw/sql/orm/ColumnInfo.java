package scw.sql.orm;

import java.lang.reflect.Field;

import scw.sql.orm.annotation.Cas;
import scw.sql.orm.annotation.Counter;

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
	
	Cas getCas();
}
