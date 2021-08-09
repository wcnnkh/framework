package scw.orm.sql;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.convert.TypeDescriptor;
import scw.core.annotation.AnnotatedElementUtils;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.orm.ObjectKeyFormat;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Unique;
import scw.sql.Sql;

public interface SqlDialect extends ObjectKeyFormat, ObjectRelationalMapping{
	SqlType getSqlType(Class<?> javaType);

	default boolean isAutoIncrement(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, AutoIncrement.class);
	}

	default boolean isAutoIncrement(Field field) {
		return (field.isSupportGetter() && isAutoIncrement(field.getGetter())
				|| (field.isSupportSetter() && isAutoIncrement(field.getSetter())));
	}

	default boolean isUnique(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Unique.class);
	}

	default boolean isUnique(Field field) {
		return (field.isSupportGetter() && isUnique(field.getGetter()))
				|| (field.isSupportSetter() && isUnique(field.getSetter()));
	}

	default Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	Object toDataBaseValue(Object value, TypeDescriptor sourceType);

	String getCharsetName(FieldDescriptor fieldDescriptor);

	String getComment(Field field);

	Map<IndexInfo, List<IndexInfo>> getIndexInfoMap(Class<?> entityClass);

	Sql toSelectByIdsSql(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException;

	<T> Sql save(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	<T> Sql delete(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException;

	<T> Sql update(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	<T> Sql toSaveOrUpdateSql(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	Sql toCreateTableSql(String tableName, Class<?> entityClass) throws SqlDialectException;

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long start, long limit) throws SqlDialectException;

	Sql getInIds(String tableName, Class<?> entityClass, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(Class<?> entityClass, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toMaxIdSql(Class<?> clazz, String tableName, Field field) throws SqlDialectException;

	TableStructureMapping getTableStructureMapping(Class<?> clazz, String tableName);
}
