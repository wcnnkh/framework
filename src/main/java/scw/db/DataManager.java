package scw.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DataManager {
	boolean save(Object bean);

	boolean update(Object bean);

	boolean delete(Object bean);

	boolean deleteById(Class<?> type, Object... params);

	boolean saveOrUpdate(Object bean);

	<T> T getById(Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds,
			Object... params);
}
