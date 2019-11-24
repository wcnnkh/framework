package scw.sql.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.core.Pagination;
import scw.core.utils.IteratorCallback;
import scw.sql.Sql;
import scw.sql.orm.result.Result;
import scw.sql.orm.result.ResultSet;

public interface ORMOperations {
	<T> T getById(Class<T> type, Object... params);

	<T> T getById(String tableName, Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	<T> List<T> getByIdList(String tableName, Class<T> type, Object... params);

	/**
	 * @param type
	 * @param tableName
	 * @param inIds
	 * @param params
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params);

	/**
	 * @param type
	 * @param inIdList
	 * @param params
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params);

	ResultSet select(Sql sql);

	<T> List<T> select(Class<T> type, Sql sql);

	<T> T selectOne(Class<T> type, Sql sql);
	
	<T> T selectOne(Class<T> type, Sql sql, T defaultValue);

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

	<T> T getMaxValue(Class<?> tableClass, String tableName, String idField);
	
	<T> T getMaxValue(Class<?> tableClass, String idField);
	
	void createTable(Class<?> tableClass);
	
	void createTable(Class<?> tableClass, String tableName);
	
	void createTable(String packageName);
	
	<T> Pagination<List<T>> select(Class<T> type, long page, int limit, Sql sql);
	
	Pagination<ResultSet> select(long page, int limit, Sql sql);
	
	Pagination<ResultSet> select(int page, int limit, Sql sql);
	
	<T> Pagination<List<T>> select(Class<T> type, int page, int limit, Sql sql);
	
	<T> void iterator(final Class<T> tableClass, final IteratorCallback<T> iterator);
	
	void iterator(Sql sql, final IteratorCallback<Result> iterator);
}
