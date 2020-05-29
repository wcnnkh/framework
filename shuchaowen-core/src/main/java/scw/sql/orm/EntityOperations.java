package scw.sql.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.core.IteratorCallback;
import scw.core.IteratorCallback.Row;
import scw.sql.Sql;
import scw.util.Pagination;

@Bean(proxy=false)
public interface EntityOperations {
	<T> T getById(Class<? extends T> type, Object... params);

	<T> T getById(String tableName, Class<? extends T> type, Object... params);

	<T> List<T> getByIdList(Class<? extends T> type, Object... params);

	<T> List<T> getByIdList(String tableName, Class<? extends T> type, Object... params);

	/**
	 * @param type
	 * @param tableName
	 * @param inPrimaryKeys
	 * @param primaryKeys
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<? extends V> type, String tableName, Collection<? extends K> inPrimaryKeys, Object... primaryKeys);

	/**
	 * @param type
	 * @param inPrimaryKeys
	 * @param primaryKeys
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<? extends V> type, Collection<? extends K> inPrimaryKeys, Object... primaryKeys);

	ResultSet select(Sql sql);

	<T> List<T> select(Class<? extends T> type, Sql sql);

	<T> T selectOne(Class<? extends T> type, Sql sql);

	<T> T selectOne(Class<? extends T> type, Sql sql, T defaultValue);

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

	<T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, String tableName, String idField);

	<T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, String idField);

	void createTable(Class<?> tableClass);

	void createTable(Class<?> tableClass, String tableName);

	void createTable(String packageName);

	<T> Pagination<T> select(Class<? extends T> type, long page, int limit, Sql sql);

	Pagination<ResultMapping> select(long page, int limit, Sql sql);
	
	<T> Pagination<T> select(Class<? extends T> type, int page, int limit, Sql sql);

	Pagination<ResultMapping> select(int page, int limit, Sql sql);

	<T> void iterator(Class<? extends T> tableClass, IteratorCallback<T> iterator);

	void iterator(Sql sql, IteratorCallback<ResultMapping> iterator);

	<T> void iterator(Sql sql, Class<? extends T> type, IteratorCallback<T> iterator);
	
	<T> void query(Class<? extends T> tableClass, IteratorCallback<Row<T>> iterator);

	void query(Sql sql, IteratorCallback<Row<ResultMapping>> iterator);

	<T> void query(Sql sql, Class<? extends T> type, IteratorCallback<Row<T>> iterator);

	TableChange getTableChange(Class<?> tableClass);

	TableChange getTableChange(Class<?> tableClass, String tableName);
}
