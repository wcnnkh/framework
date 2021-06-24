package scw.orm.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scw.convert.TypeDescriptor;
import scw.lang.Nullable;
import scw.mapper.Field;
import scw.orm.EntityOperations;
import scw.orm.MaxValueFactory;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.SqlOperations;
import scw.util.Pagination;
import scw.util.stream.AutoCloseStream;
import scw.util.stream.StreamProcessorSupport;

public interface SqlTemplate extends EntityOperations, SqlOperations, MaxValueFactory {
	SqlDialect getSqlDialect();

	default boolean createTable(Class<?> entityClass) {
		if (entityClass == null) {
			return false;
		}

		return createTable(null, entityClass);
	}

	boolean createTable(@Nullable String tableName, Class<?> entityClass);

	@Override
	default <T> boolean save(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return save(null, entityClass, entity);
	}

	<T> boolean save(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return delete(null, entityClass, entity);
	}

	<T> boolean delete(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Override
	default boolean deleteById(Class<?> entityClass, Object... ids) {
		if (entityClass == null) {
			return false;
		}

		return deleteById(null, entityClass, ids);
	}

	boolean deleteById(@Nullable String tableName, Class<?> entityClass, Object... ids);

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return update(null, entityClass, entity);
	}

	<T> boolean update(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Override
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return saveOrUpdate(null, entityClass, entity);
	}

	<T> boolean saveOrUpdate(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Nullable
	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(null, entityClass, ids);
	}

	@Nullable
	<T> T getById(@Nullable String tableName, Class<? extends T> entityClass, Object... ids);

	default <T> List<T> getByIdList(Class<? extends T> entityClass, Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	<T> List<T> getByIdList(@Nullable String tableName, Class<? extends T> entityClass, Object... ids);

	default <K, V> Map<K, V> getInIds(Class<? extends V> type, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		return getInIds(null, type, inPrimaryKeys, primaryKeys);
	}

	<K, V> Map<K, V> getInIds(String tableName, Class<? extends V> entityClass, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys);

	<T> AutoCloseStream<T> streamQuery(Connection connection, TypeDescriptor resultTypeDescriptor, Sql sql);

	default <T> AutoCloseStream<T> streamQuery(TypeDescriptor resultTypeDescriptor, Sql sql) {
		try {
			Stream<T> stream = streamProcess((connection) -> {
				return streamQuery(connection, resultTypeDescriptor, sql);
			}, () -> sql.toString());
			return StreamProcessorSupport.autoClose(stream);
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	default <T> AutoCloseStream<T> streamQuery(Connection connection, Class<? extends T> resultType, Sql sql) {
		return streamQuery(connection, TypeDescriptor.valueOf(resultType), sql);
	}

	default <T> AutoCloseStream<T> streamQuery(Class<? extends T> resultType, Sql sql) {
		return streamQuery(TypeDescriptor.valueOf(resultType), sql);
	}

	@Nullable
	default <T> T queryFirst(Class<? extends T> resultType, Sql sql) {
		Stream<T> stream = streamQuery(resultType, sql);
		return stream.findFirst().orElse(null);
	}

	default <T> List<T> query(TypeDescriptor typeDescriptor, Sql sql) {
		Stream<T> stream = streamQuery(typeDescriptor, sql);
		return stream.collect(Collectors.toList());
	}

	default <T> List<T> query(Class<? extends T> entityClass, Sql sql) {
		return query(TypeDescriptor.valueOf(entityClass), sql);
	}

	<T> Pagination<T> paginationQuery(TypeDescriptor resultType, Sql sql, long page, int limit);

	default <T> Pagination<T> paginationQuery(Class<? extends T> resultType, Sql sql, long page, int limit) {
		return paginationQuery(TypeDescriptor.valueOf(resultType), sql, page, limit);
	}

	/**
	 * 获取表的变更
	 * 
	 * @param tableClass
	 * @return
	 */
	default TableChanges getTableChanges(Class<?> tableClass) {
		return getTableChanges(tableClass, null);
	}

	/**
	 * 获取表的变更
	 * 
	 * @param tableClass
	 * @param tableName
	 * @return
	 */
	TableChanges getTableChanges(Class<?> tableClass, @Nullable String tableName);

	/**
	 * 获取对象指定字段的最大值
	 * 
	 * @param <T>
	 * @param type
	 * @param tableClass
	 * @param field
	 * @return
	 */
	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, Field field) {
		return getMaxValue(type, tableClass, null, field);
	}

	/**
	 * 获取对象指定字段的最大值
	 * 
	 * @param type
	 * @param tableClass
	 * @param tableName
	 * @param field
	 * @return
	 */
	@Nullable
	<T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, @Nullable String tableName, Field field);
}
