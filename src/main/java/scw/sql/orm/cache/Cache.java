package scw.sql.orm.cache;

public interface Cache {
	
	<T> T get(Class<T> type, String key);
	
	void delete(String key);

	void add(String key, Object bean);

	void set(String key, Object bean);
}
