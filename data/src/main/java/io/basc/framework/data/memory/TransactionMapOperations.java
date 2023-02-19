package io.basc.framework.data.memory;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionUtils;

/**
 * 事务缓存
 * 
 * @author wcnnkh
 *
 */
public final class TransactionMapOperations<K, V> extends AbstractMapOperations<K, V> {
	private final Object name;

	public TransactionMapOperations(Object name) {
		this.name = name;
	};

	public void clear() {
		Map<K, V> map = getMap();
		if (map != null) {
			map.clear();
		}
	}

	public Map<K, V> getMap() {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return null;
		}

		return transaction.getResource(name);
	}

	@Override
	protected Map<K, V> createMap() {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return null;
		}

		Map<K, V> map = transaction.getResource(name);
		if (map == null) {
			map = new HashMap<>(8);
			transaction.registerResource(name, map);
		}
		return map;
	}
}
