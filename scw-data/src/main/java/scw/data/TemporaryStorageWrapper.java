package scw.data;

import scw.core.utils.StringUtils;

public final class TemporaryStorageWrapper extends AbstractTemporaryStorageWrapper<TemporaryStorage> {
	private String keyPrefix;
	private TemporaryStorage cache;
	private boolean transaction;

	public TemporaryStorageWrapper(TemporaryStorage cache, boolean transaction, String keyPrefix) {
		this.cache = cache;
		this.transaction = transaction;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public TemporaryStorage getTargetStorage() {
		return cache;
	}

	@Override
	public boolean isTransaction() {
		return transaction;
	}

	@Override
	public String formatKey(String key) {
		return key == null ? key : (keyPrefix + key);
	}

	public <T> java.util.Map<String, T> get(java.util.Collection<String> keys) {
		if (StringUtils.isEmpty(keyPrefix)) {
			return getTargetStorage().get(keys);
		}
		return super.get(keys);
	};
}
