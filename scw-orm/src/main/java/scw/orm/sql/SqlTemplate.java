package scw.orm.sql;

import java.util.List;

import scw.lang.Nullable;
import scw.orm.EntityOperations;
import scw.sql.Sql;
import scw.sql.SqlOperations;

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

	<T> List<T> query(Class<? extends T> entityClass, Sql sql);
}
