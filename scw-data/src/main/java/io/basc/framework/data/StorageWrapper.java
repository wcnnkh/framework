package io.basc.framework.data;

import io.basc.framework.core.utils.StringUtils;

public final class StorageWrapper extends AbstractStorageWrapper<Storage> {
	private final Storage cache;
	private final boolean transaction;
	private final String keyPrefix;

	public StorageWrapper(Storage cache, boolean transaction, String keyPrefix) {
		this.cache = cache;
		this.transaction = transaction;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public Storage getTargetStorage() {
		return cache;
	}

	@Override
	public boolean isTransaction() {
		return transaction;
	}

	@Override
	public String formatKey(String key) {
		return keyPrefix == null ? key : (keyPrefix + key);
	}

	public <T> java.util.Map<String, T> get(java.util.Collection<String> keys) {
		if (StringUtils.isEmpty(keyPrefix)) {
			return getTargetStorage().get(keys);
		}
		return super.get(keys);
	};
}
