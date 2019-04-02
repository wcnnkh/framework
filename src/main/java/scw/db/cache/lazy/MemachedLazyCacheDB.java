package scw.db.cache.lazy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanFieldListen;
import scw.memcached.Memcached;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public abstract class MemachedLazyCacheDB extends AbstractLazyCacheDB {
	private final Memcached memcached;
	private final int exp;

	public MemachedLazyCacheDB(Memcached memcached, int exp) {
		this.memcached = memcached;
		this.exp = exp;   
	}

	@Override
	protected <T> T get(Class<T> type, String key) {
		T t = memcached.getAndTocuh(key, exp);
		if (t == null) {
			return null;
		}

		if (t instanceof BeanFieldListen) {
			((BeanFieldListen) t).start_field_listen();
		}
		return t;
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public void add(final String key, Object data) {
		memcached.add(key, exp, data);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				memcached.delete(key);
			}
		});
	}

	public void set(final String key, Object data) {

		memcached.set(key, exp, data);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterProcess() {
				memcached.delete(key);
			}
		});
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		Map<String, T> map = memcached.get(keys);
		if (map != null && !map.isEmpty()) {
			for (Entry<String, T> entry : map.entrySet()) {
				T v = entry.getValue();
				if (v == null) {
					map.remove(entry.getKey());
					continue;
				}

				if (v instanceof BeanFieldListen) {
					((BeanFieldListen) v).start_field_listen();
				}
			}
		}
		return map;
	}
}
