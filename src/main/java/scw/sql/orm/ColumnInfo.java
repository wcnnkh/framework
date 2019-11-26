package scw.sql.orm;

import java.lang.reflect.Field;

import scw.core.reflect.AnnotationFactory;
import scw.orm.sql.dialect.SqlType;
import scw.sql.orm.enums.CasType;

public interface ColumnInfo extends AnnotationFactory {
	String getName();

	boolean isPrimaryKey();

	SqlType getSqlType();

	boolean isNullAble();

	boolean isDataBaseType();

	Field getField();

	boolean isUnique();

	boolean isAutoIncrement();

	CasType getCasType();

	Object get(Object bean) throws Exception;

	void set(Object bean, Object value) throws Exception;

	Object toSqlField(Object value) throws Exception;

	String getCharsetName();
}
