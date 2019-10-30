package scw.data.memcached;

import java.util.Collection;
import java.util.Map;

import scw.data.cas.CASOperations;

public abstract class AbstractMemcachedWrapper implements Memcached {

	public abstract Memcached getTargetMemcached();

	public <T> T get(String key) {
		return getTargetMemcached().get(key);
	}

	public <T> T getAndTouch(String key, int newExp) {
		return getTargetMemcached().getAndTouch(key, newExp);
	}

	public boolean set(String key, Object data) {
		return getTargetMemcached().set(key, data);
	}

	public boolean set(String key, int exp, Object data) {
		return getTargetMemcached().set(key, exp, data);
	}

	public boolean add(String key, Object value) {
		return getTargetMemcached().add(key, value);
	}

	public boolean add(String key, int exp, Object data) {
		return getTargetMemcached().add(key, data);
	}

	public boolean touch(String key, int exp) {
		return getTargetMemcached().touch(key, exp);
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		return getTargetMemcached().get(keyCollections);
	}

	public long incr(String key, long delta) {
		return getTargetMemcached().incr(key, delta);
	}

	public long incr(String key, long delta, long initValue) {
		return getTargetMemcached().incr(key, delta, initValue);
	}

	public long decr(String key, long delta) {
		return getTargetMemcached().decr(key, delta);
	}

	public long decr(String key, long delta, long initValue) {
		return getTargetMemcached().decr(key, delta, initValue);
	}

	public boolean delete(String key) {
		return getTargetMemcached().delete(key);
	}

	public boolean isExist(String key) {
		return getTargetMemcached().delete(key);
	}

	/**
	 * 默认的实现每次都会重新创建一个对象
	 */
	public CASOperations getCASOperations() {
		return getTargetMemcached().getCASOperations();
	}

	public long incr(String key, long incr, long initValue, int exp) {
		return getTargetMemcached().incr(key, incr, initValue, exp);
	}

	public long decr(String key, long decr, long initValue, int exp) {
		return getTargetMemcached().decr(key, decr, initValue, exp);
	}
}
