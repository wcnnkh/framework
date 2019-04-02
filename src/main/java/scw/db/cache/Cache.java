package scw.db.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Cache {

	void save(Object bean) throws Throwable;

	void update(Object bean) throws Throwable;

	void delete(Object bean) throws Throwable;

	void saveOrUpdate(Object bean) throws Throwable;

	<T> T getById(Class<T> type, Object... params) throws Throwable;

	<T> List<T> getByIdList(Class<T> type, Object... params) throws Throwable;

	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) throws Throwable;
}
