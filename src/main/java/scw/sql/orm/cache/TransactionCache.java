package scw.sql.orm.cache;

import java.util.Collection;
import java.util.Map;

import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public class TransactionCache implements Cache {
	private final Cache cache;

	public TransactionCache(Memcached memcached, int exp) {
		this.cache = new MemcachedCache(memcached, exp);
	}

	public TransactionCache(Redis redis, int exp) {
		this.cache = new RedisCache(redis, exp);
	}

	public <T> T get(Class<T> type, String key) {
		return cache.get(type, key);
	}

	public void delete(String key) {
		cache.delete(key);
	}

	public void add(final String key, final Object bean) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterProcess() {
				cache.add(key, bean);
			}

			@Override
			public void afterRollback() {
				cache.delete(key);
			}
		});
	}

	public void set(final String key, final Object bean) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterProcess() {
				cache.set(key, bean);
			}

			@Override
			public void afterRollback() {
				cache.delete(key);
			}
		});
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		return cache.getMap(type, keys);
	}
}
