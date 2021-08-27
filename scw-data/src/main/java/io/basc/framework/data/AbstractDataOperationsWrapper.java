package io.basc.framework.data;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractDataOperationsWrapper implements DataOperations {
	public abstract DataOperations getDataOperations();

	public <T> T get(String key) {
		return getDataOperations().get(key);
	}

	public <T> T getAndTouch(String key, int newExp) {
		return getDataOperations().getAndTouch(key, newExp);
	}

	public void set(String key, Object value) {
		getDataOperations().set(key, value);
	}

	public void set(String key, int exp, Object value) {
		getDataOperations().set(key, exp, value);
	}

	public boolean add(String key, Object value) {
		return getDataOperations().add(key, value);
	}

	public boolean add(String key, int exp, Object value) {
		return getDataOperations().add(key, exp, value);
	}

	public boolean touch(String key, int exp) {
		return getDataOperations().touch(key, exp);
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		return getDataOperations().get(keyCollections);
	}

	public boolean delete(String key) {
		return getDataOperations().delete(key);
	}

	public boolean isExist(String key) {
		return getDataOperations().isExist(key);
	}

	public long incr(String key, long delta) {
		return getDataOperations().incr(key, delta);
	}

	public long incr(String key, long delta, long initialValue) {
		return getDataOperations().incr(key, delta, initialValue);
	}

	public long incr(String key, long delta, long initialValue, int exp) {
		return getDataOperations().incr(key, delta, initialValue, exp);
	}

	public long decr(String key, long delta) {
		return getDataOperations().decr(key, delta);
	}

	public long decr(String key, long delta, long initialValue) {
		return getDataOperations().decr(key, delta, initialValue);
	}

	public long decr(String key, long delta, long initialValue, int exp) {
		return getDataOperations().decr(key, delta, initialValue, exp);
	}

	public void delete(Collection<String> keys) {
		getDataOperations().delete(keys);
	}
}
