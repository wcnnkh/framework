package scw.orm.sql;

import scw.core.annotation.AnnotatedElementUtils;
import scw.mapper.FieldDescriptor;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Counter;
import scw.sql.Sql;

public interface SqlDialect {
	default boolean isAutoIncrement(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, AutoIncrement.class);
	}

	default Counter getCounter(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.findMergedAnnotation(fieldDescriptor, Counter.class);
	}
	
	Sql getById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException;

	<T> Sql save(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	<T> Sql delete(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException;

	<T> Sql update(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	<T> Sql saveOrUpdate(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException;

	Sql createTable(String tableName, Class<?> entityClass) throws SqlDialectException;

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long start, int limit) throws SqlDialectException;
}
