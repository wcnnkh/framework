package scw.data.memcached;

import java.util.Collection;
import java.util.Map;

import scw.beans.annotation.AutoImpl;
import scw.data.cas.CASOperations;

@AutoImpl(implClassName = { "scw.data.memcached.x.XMemcached" })
public interface Memcached {
	<T> T get(String key);

	<T> T getAndTouch(String key, int newExp);

	boolean set(String key, Object data);

	boolean set(String key, int exp, Object data);

	boolean add(String key, Object value);

	boolean add(String key, int exp, Object data);

	boolean touch(String key, int exp);

	<T> Map<String, T> get(Collection<String> keyCollections);

	long incr(String key, long incr);

	long incr(String key, long incr, long initValue);

	long decr(String key, long decr);

	long decr(String key, long decr, long initValue);

	boolean delete(String key);

	boolean delete(String key, long cas, long opTimeout);

	boolean isExist(String key);

	CASOperations getCASOperations();
}
