package scw.id.db;

import scw.core.utils.ClassUtils;
import scw.data.redis.Redis;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.locks.RedisLockFactory;
import scw.sql.orm.ORMOperations;

public final class RedisTableIdFactory extends AbstractTableIdFactory {
	private final Redis redis;
	private final LockFactory lockFactory;

	public RedisTableIdFactory(ORMOperations db, Redis redis) {
		super(db);
		this.redis = redis;
		this.lockFactory = new RedisLockFactory(redis);
	}

	private String getCacheKey(Class<?> tableClass, String fieldName) {
		StringBuilder sb = new StringBuilder(64);
		sb.append(this.getClass().getName());
		sb.append("&");
		sb.append(ClassUtils.getProxyRealClassName(tableClass));
		sb.append("&");
		sb.append(fieldName);
		return sb.toString();
	}

	public long generator(Class<?> tableClass, String fieldName) {
		String key = getCacheKey(tableClass, fieldName);
		if (!redis.getStringOperations().exists(key)) {
			// 不存在
			Lock lock = lockFactory.getLock(key + "&lock");
			try {
				lock.lock();
				if (!redis.getStringOperations().exists(key)) {
					long maxId = getMaxId(tableClass, fieldName);
					return redis.getStringOperations().incr(key, 1, maxId + 1);
				}
			} finally {
				lock.unlock();
			}
		}
		return redis.getStringOperations().incr(key);
	}

}
