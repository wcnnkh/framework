package scw.sql.orm.cache;

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

	public void add(final String key, Object bean) {
		cache.add(key, bean);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeRollback() throws Throwable {
				cache.delete(key);
			}
		});
	}

	public void set(final String key, Object bean) {
		cache.set(key, bean);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeRollback() throws Throwable {
				cache.delete(key);
			}
		});
	}
}
