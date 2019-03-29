package scw.sql.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.sql.Sql;
import scw.sql.orm.result.ResultSet;

public interface ORMOperations {
	<T> T getById(Class<T> type, Object... params);

	<T> T getById(String tableName, Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	<T> List<T> getByIdList(String tableName, Class<T> type, Object... params);

	<K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params);

	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params);

	ResultSet select(Sql sql);

	<T> List<T> select(Class<T> type, Sql sql);

	<T> T selectOne(Class<T> type, Sql sql);

	boolean save(Object bean);

	boolean save(Object bean, String tableName);

	boolean update(Object bean);

	boolean update(Object bean, String tableName);

	boolean delete(Object bean);

	boolean delete(Object bean, String tableName);

	boolean deleteById(Class<?> type, Object... params);

	boolean deleteById(String tableName, Class<?> type, Object... params);

	boolean saveOrUpdate(Object bean);

	boolean saveOrUpdate(Object bean, String tableName);
}
