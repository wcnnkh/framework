package io.basc.framework.data;

import io.basc.framework.transaction.DefaultTransactionLifecycle;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractStorageWrapper<C extends Storage> implements Storage {
	public abstract C getTargetStorage();

	public abstract boolean isTransaction();

	public abstract String formatKey(String key);

	@Override
	public String toString() {
		return getTargetStorage().toString();
	}
	
	public <T> T get(String key) {
		return getTargetStorage().get(formatKey(key));
	}

	public <T> Map<String, T> get(Collection<String> keys) {
		Map<String, String> keyMapping = new LinkedHashMap<String, String>();
		for (String key : keys) {
			keyMapping.put(formatKey(key), key);
		}

		Map<String, T> valueMap = getTargetStorage().get(keyMapping.keySet());
		Map<String, T> result = new LinkedHashMap<String, T>();
		for (Entry<String, T> entry : valueMap.entrySet()) {
			String key = keyMapping.get(entry.getKey());
			if (key == null) {
				continue;
			}

			result.put(key, entry.getValue());
		}
		return getTargetStorage().get(keys);
	}

	public boolean add(String key, Object value) {
		transactionDelete(key);
		return getTargetStorage().add(formatKey(key), value);
	}

	public void set(String key, Object value) {
		transactionDelete(key);
		getTargetStorage().set(formatKey(key), value);
	}

	public boolean isExist(String key) {
		return getTargetStorage().isExist(formatKey(key));
	}

	public boolean delete(String key) {
		return getTargetStorage().delete(formatKey(key));
	}

	public void delete(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return;
		}

		Collection<String> ks = new LinkedList<String>();
		for (String key : keys) {
			ks.add(formatKey(key));
		}
		getTargetStorage().delete(ks);
	}

	protected void transactionDelete(final String key) {
		TransactionManager manager = TransactionUtils.getManager();
		if (isTransaction() && manager.hasTransaction()) {
			manager.getTransaction().addLifecycle(new DefaultTransactionLifecycle() {
				@Override
				public void afterRollback() {
					getTargetStorage().delete(formatKey(key));
				}
			});
		}
	}
}
