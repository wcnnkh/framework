package scw.db.cache.lazy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Constants;
import scw.db.cache.CacheUtils;
import scw.redis.Redis;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public abstract class RedisLazyCacheDB extends AbstractLazyCacheDB {
	private final Redis redis;
	private final int exp;

	public RedisLazyCacheDB(Redis redis, int exp) {
		this.redis = redis;
		this.exp = exp;
	}

	public <T> T get(Class<T> type, String key) {
		byte[] data = redis.getAndTouch(key.getBytes(Constants.DEFAULT_CHARSET), exp);
		return CacheUtils.decode(type, data);
	}

	public void delete(String key) {
		redis.delete(key.getBytes(Constants.DEFAULT_CHARSET));
	}

	public void add(String key, Object bean) {
		final byte[] bk = key.getBytes(Constants.DEFAULT_CHARSET);
		redis.set(bk, CacheUtils.encode(bean), Redis.NX.getBytes(Constants.DEFAULT_CHARSET),
				Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				redis.delete(bk);
			}
		});
	}

	public void set(String key, Object bean) {
		final byte[] bk = key.getBytes(Constants.DEFAULT_CHARSET);
		redis.set(bk, CacheUtils.encode(bean), Redis.XX.getBytes(Constants.DEFAULT_CHARSET),
				Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				redis.delete(bk);
			}
		});
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		if (keys.isEmpty()) {
			return null;
		}

		byte[][] bKeys = new byte[keys.size()][];
		Iterator<String> iterator = keys.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			bKeys[i] = iterator.next().getBytes(Constants.DEFAULT_CHARSET);
		}

		Map<byte[], byte[]> map = redis.get(bKeys);
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>(map.size(), 1);
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			String key = new String(entry.getKey(), Constants.DEFAULT_CHARSET);
			T v = CacheUtils.decode(type, entry.getValue());
			valueMap.put(key, v);
		}
		return valueMap;
	}
}
