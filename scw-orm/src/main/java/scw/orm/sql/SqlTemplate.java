package scw.orm.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scw.convert.TypeDescriptor;
import scw.lang.Nullable;
import scw.orm.EntityOperations;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.SqlOperations;
import scw.sql.SqlProcessor;
import scw.util.Pagination;

public interface SqlTemplate extends EntityOperations, SqlOperations {
	default boolean createTable(Class<?> entityClass) {
		return createTable(null, entityClass);
	}

	boolean createTable(@Nullable String tableName, Class<?> entityClass);

	default boolean save(Object entity) {
		return save(null, entity);
	}

	boolean save(@Nullable String tableName, Object entity);

	@Override
	default boolean delete(Object entity) {
		return delete(null, entity);
	}

	boolean delete(@Nullable String tableName, Object entity);

	@Override
	default boolean deleteById(Class<?> entityClass, Object... ids) {
		return deleteById(null, entityClass, ids);
	}

	boolean deleteById(@Nullable String tableName, Class<?> entityClass, Object... ids);

	@Override
	default boolean update(Object entity) {
		return update(null, entity);
	}

	boolean update(@Nullable String tableName, Object entity);

	@Override
	default boolean saveOrUpdate(Object entity) {
		return saveOrUpdate(entity);
	}

	boolean saveOrUpdate(@Nullable String tableName, Object entity);

	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(null, entityClass, ids);
	}

	<T> T getById(@Nullable String tableName, Class<? extends T> entityClass, Object... ids);

	<T> Stream<T> streamQuery(Connection connection, TypeDescriptor resultTypeDescriptor, Sql sql);

	default <T> Stream<T> streamQuery(TypeDescriptor resultTypeDescriptor, Sql sql) {
		try {
			return process(new SqlProcessor<Connection, Stream<T>>() {

				@Override
				public Stream<T> process(Connection source) throws SQLException {
					return streamQuery(source, resultTypeDescriptor, sql);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	default <T> Stream<T> streamQuery(Connection connection, Class<? extends T> resultType, Sql sql) {
		return streamQuery(connection, TypeDescriptor.valueOf(resultType), sql);
	}

	default <T> Stream<T> streamQuery(Class<? extends T> resultType, Sql sql) {
		return streamQuery(TypeDescriptor.valueOf(resultType), sql);
	}

	default <T> List<T> query(TypeDescriptor typeDescriptor, Sql sql) {
		Stream<T> stream = streamQuery(typeDescriptor, sql);
		return stream.collect(Collectors.toList());
	}

	default <T> List<T> query(Class<? extends T> entityClass, Sql sql) {
		return query(TypeDescriptor.valueOf(entityClass), sql);
	}

	<T> Pagination<T> queryPagination(TypeDescriptor resultType, Sql sql, long page, int limit);

	default <T> Pagination<T> queryPagination(Class<? extends T> resultType, Sql sql, long page, int limit) {
		return queryPagination(TypeDescriptor.valueOf(resultType), sql, page, limit);
	}
}
