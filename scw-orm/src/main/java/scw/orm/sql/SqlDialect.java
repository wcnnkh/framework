package scw.orm.sql;

import scw.mapper.FieldDescriptor;
import scw.sql.Sql;

public interface SqlDialect {
	boolean isAutoIncrement(FieldDescriptor fieldDescriptor);

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
