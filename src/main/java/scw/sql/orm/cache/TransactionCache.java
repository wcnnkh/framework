package scw.sql.orm.cache;

import java.util.Collection;
import java.util.Map;

import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.transaction.TransactionException;
import scw.transaction.sql.SqlTransactionUtils;
import scw.transaction.support.TransactionSynchronization;

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
		SqlTransactionUtils.transactionSynchronization(new TransactionSynchronization() {

			public void rollback() throws TransactionException {
				cache.delete(key);
			}

			public void process() throws TransactionException {
				cache.add(key, bean);
			}

			public void end() {
			}
		});
	}

	public void set(final String key, final Object bean) {
		SqlTransactionUtils.transactionSynchronization(new TransactionSynchronization() {

			public void rollback() throws TransactionException {
				cache.delete(key);
			}

			public void process() throws TransactionException {
				cache.set(key, bean);
			}

			public void end() {
			}
		});
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		return cache.getMap(type, keys);
	}
}
