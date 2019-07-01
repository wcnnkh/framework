package scw.sql.orm;

import java.lang.annotation.Annotation;

import scw.core.FieldSetterListen;

public interface TableInfo {
	String getDefaultName();

	ColumnInfo getColumnInfo(String fieldName);

	ColumnInfo[] getColumns();

	ColumnInfo[] getPrimaryKeyColumns();

	ColumnInfo[] getNotPrimaryKeyColumns();

	boolean isTable();

	/*
	 * 这些字段都是实体类，并且对应着表
	 * 
	 * @return
	 */
	ColumnInfo[] getTableColumns();

	Class<?> getSource();

	<T> T newInstance();

	Class<? extends FieldSetterListen> getProxyClass();

	ColumnInfo getAutoIncrement();

	<T extends Annotation> T getAnnotation(Class<T> type);

	String getName(Object bean);
}
