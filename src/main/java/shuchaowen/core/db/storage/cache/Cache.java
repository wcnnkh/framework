package shuchaowen.core.db.storage.cache;

public interface Cache {
	<T> T getAndTouch(Class<T> type, String key, int exp);
	
	void set(String key, int exp, Object data);
	
	void add(String key, int exp, Object data);

	void delete(String ...key);
}
