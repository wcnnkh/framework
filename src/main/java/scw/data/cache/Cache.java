package scw.data.cache;

public interface Cache<T> {
	boolean save(String key);

	boolean update(String key, T value);

	boolean delete(String key);

	boolean saveOrUpdate(String key, T value);

	T get(String key);
}
