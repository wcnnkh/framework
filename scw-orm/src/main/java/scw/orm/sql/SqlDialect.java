package scw.orm.sql;

import java.util.List;
import java.util.Map;

import scw.core.annotation.AnnotatedElementUtils;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Counter;
import scw.orm.sql.annotation.Unique;
import scw.sql.Sql;

public interface SqlDialect {
	SqlType getSqlType(Class<?> javaType);

	default boolean isAutoIncrement(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor,
				AutoIncrement.class);
	}

	default Counter getCounter(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.findMergedAnnotation(fieldDescriptor,
				Counter.class);
	}

	default boolean isUnique(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Unique.class);
	}
	
	boolean isNullable(FieldDescriptor fieldDescriptor);
	
	String getComment(Field field);

	Map<IndexInfo, List<IndexInfo>> getIndexInfoMap(Class<?> entityClass);

	Sql getById(String tableName, Class<?> entityClass, Object... ids)
			throws SqlDialectException;

	<T> Sql save(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException;

	<T> Sql delete(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException;

	Sql deleteById(String tableName, Class<?> entityClass, Object... ids)
			throws SqlDialectException;

	<T> Sql update(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException;

	<T> Sql saveOrUpdate(String tableName, Class<? extends T> entityClass,
			T entity) throws SqlDialectException;

	Sql createTable(String tableName, Class<?> entityClass)
			throws SqlDialectException;

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long start, int limit)
			throws SqlDialectException;
}
