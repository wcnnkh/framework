package scw.sql.orm;

import java.util.List;

public interface ORMOperations {
	<T> T getById(Class<T> type, Object... params);

	<T> T getById(String tableName, Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	<T> List<T> getByIdList(String tableName, Class<T> type, Object... params);

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
