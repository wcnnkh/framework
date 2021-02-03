package scw.sql.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.core.IteratorCallback;
import scw.core.IteratorCallback.Row;
import scw.sql.Sql;
import scw.util.Pagination;

public interface EntityOperations {
	/**
	 * 根据主键获取一条数据
	 * @param type
	 * @param params
	 * @return
	 */
	<T> T getById(Class<T> type, Object... params);

	/**
	 * 根据主键获取一条数据
	 * @param tableName 指定表名
	 * @param type
	 * @param params
	 * @return
	 */
	<T> T getById(String tableName, Class<T> type, Object... params);

	/**
	 * 根据主键获取多条数据
	 * @param type
	 * @param params
	 * @return
	 */
	<T> List<T> getByIdList(Class<T> type, Object... params);

	/**
	 * 根据主键获取多条数据
	 * @param tableName 指定表名
	 * @param type
	 * @param params
	 * @return
	 */
	<T> List<T> getByIdList(String tableName, Class<T> type, Object... params);

	/**
	 * @param type
	 * @param tableName
	 * @param inPrimaryKeys
	 * @param primaryKeys
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inPrimaryKeys, Object... primaryKeys);

	/**
	 * @param type
	 * @param inPrimaryKeys
	 * @param primaryKeys
	 * @return 不会为空
	 */
	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inPrimaryKeys, Object... primaryKeys);

	/**
	 * 执行Select语句
	 * @param sql
	 * @return
	 */
	ResultSet select(Sql sql);

	/**
	 * 执行select语句并返回指定类型的数据列表
	 * @param type
	 * @param sql
	 * @return
	 */
	<T> List<T> select(Class<T> type, Sql sql);

	/**
	 * 执行select语句并返回指定类型的数据
	 * @param type
	 * @param sql
	 * @return
	 */
	<T> T selectOne(Class<T> type, Sql sql);

	/**
	 * 执行select语句并返回指定类型的数据
	 * @param type
	 * @param sql
	 * @param defaultValue
	 * @return
	 */
	<T> T selectOne(Class<T> type, Sql sql, T defaultValue);

	/**
	 * 保存一个对象
	 * @param bean
	 * @return
	 */
	boolean save(Object bean);

	/**
	 * 保存一个对象并指定表名
	 * @param bean
	 * @param tableName 指定表名
	 * @return
	 */
	boolean save(Object bean, String tableName);

	/**
	 * 更新一个对象并指定表名
	 * @param bean
	 * @return
	 */
	boolean update(Object bean);

	boolean update(Object bean, String tableName);

	/**
	 * 删除一个对象
	 * @param bean
	 * @return
	 */
	boolean delete(Object bean);

	boolean delete(Object bean, String tableName);

	/**
	 * 根据主键删除对象
	 * @param type
	 * @param params
	 * @return
	 */
	boolean deleteById(Class<?> type, Object... params);

	boolean deleteById(String tableName, Class<?> type, Object... params);

	/**
	 * 保存或更新对象
	 * @param bean
	 * @return
	 */
	boolean saveOrUpdate(Object bean);

	boolean saveOrUpdate(Object bean, String tableName);

	/**
	 * 获取对象指定字段的最大值
	 * @param type
	 * @param tableClass
	 * @param tableName
	 * @param idField
	 * @return
	 */
	<T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String idField);

	/**
	 * 获取对象指定字段的最大值
	 * @param type
	 * @param tableClass
	 * @param idField
	 * @return
	 */
	<T> T getMaxValue(Class<T> type, Class<?> tableClass, String idField);

	/**
	 * 创建表
	 * @param tableClass
	 * @return
	 */
	boolean createTable(Class<?> tableClass);

	/**
	 * 创建表
	 * @param tableClass
	 * @param tableName
	 * @return
	 */
	boolean createTable(Class<?> tableClass, String tableName);

	/**
	 * 扫描指定名下的@Table并创建表
	 * @param packageName
	 */
	void createTable(String packageName);

	/**
	 * 分页查询
	 * @param type
	 * @param page
	 * @param limit
	 * @param sql
	 * @return
	 */
	<T> Pagination<T> select(Class<T> type, long page, int limit, Sql sql);

	/**
	 * 分页查询
	 * @param page
	 * @param limit
	 * @param sql
	 * @return
	 */
	Pagination<ResultMapping> select(long page, int limit, Sql sql);
	
	/**
	 * 分页查询
	 * @param type
	 * @param page
	 * @param limit
	 * @param sql
	 * @return
	 */
	<T> Pagination<T> select(Class<T> type, int page, int limit, Sql sql);

	/**
	 * 分页查询
	 * @param page
	 * @param limit
	 * @param sql
	 * @return
	 */
	Pagination<ResultMapping> select(int page, int limit, Sql sql);

	/**
	 * 迭代一个表的数据
	 * @param tableClass
	 * @param iterator
	 */
	<T> void iterator(Class<T> tableClass, IteratorCallback<T> iterator);

	/**
	 * 根据sql语句迭代数据
	 * @param sql
	 * @param iterator
	 */
	void iterator(Sql sql, IteratorCallback<ResultMapping> iterator);

	/**
	 * 根据sql语句迭代数据
	 * @param sql
	 * @param type
	 * @param iterator
	 */
	<T> void iterator(Sql sql, Class<T> type, IteratorCallback<T> iterator);
	
	<T> void query(Class<T> tableClass, IteratorCallback<Row<T>> iterator);

	void query(Sql sql, IteratorCallback<Row<ResultMapping>> iterator);

	<T> void query(Sql sql, Class<T> type, IteratorCallback<Row<T>> iterator);

	/**
	 * 获取表的变更
	 * @param tableClass
	 * @return
	 */
	TableChanges getTableChanges(Class<?> tableClass);

	/**
	 * 获取表的变更
	 * @param tableClass
	 * @param tableName
	 * @return
	 */
	TableChanges getTableChanges(Class<?> tableClass, String tableName);
}
