package scw.data.memcached;

import java.util.Collection;
import java.util.Map;

public interface Memcached {
	<T> T get(String key);

	<T> T getAndTouch(String key, int newExp);

	<T> CAS<T> gets(String key);

	boolean set(String key, Object data);

	boolean set(String key, int exp, Object data);

	boolean add(String key, Object value);

	boolean add(String key, int exp, Object data);

	boolean cas(String key, Object data, long cas);

	boolean cas(String key, int exp, Object data, long cas);

	boolean touch(String key, int exp);

	<T> Map<String, T> get(Collection<String> keyCollections);

	<T> Map<String, CAS<T>> gets(Collection<String> keyCollections);

	long incr(String key, long incr);

	long incr(String key, long incr, long initValue);

	long decr(String key, long decr);

	long decr(String key, long decr, long initValue);

	boolean delete(String key);

	boolean delete(String key, long cas, long opTimeout);
}
