package io.basc.framework.data;

public abstract class AbstractTemporaryStorageWrapper<C extends TemporaryStorage> extends AbstractStorageWrapper<C>
		implements TemporaryStorage {

	public <T> T getAndTouch(String key, long exp) {
		return getTargetStorage().getAndTouch(formatKey(key), exp);
	}

	public boolean touch(String key, long exp) {
		return getTargetStorage().touch(formatKey(key), exp);
	}

	public boolean add(String key, long exp, Object value) {
		transactionDelete(key);
		return getTargetStorage().add(formatKey(key), exp, value);
	}

	public void set(String key, long exp, Object value) {
		transactionDelete(key);
		getTargetStorage().set(formatKey(key), exp, value);
	}

}
