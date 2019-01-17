package scw.utils.id.db;

import java.util.Arrays;

import scw.common.utils.ClassUtils;
import scw.db.DB;
import scw.utils.locks.Lock;
import scw.utils.locks.RedisLock;
import scw.utils.redis.Redis;

public class RedisTableIdFactory extends AbstractTableIdFactory {
	private static final String INCR_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('incr', KEYV[1]) else local newValue = ARGS[1] + 1; redis.call('set', KEYS[1], newValue) return newValue end";
	private final Redis redis;

	public RedisTableIdFactory(DB db, Redis redis) {
		super(db);
		this.redis = redis;
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
		if (!redis.exists(key)) {
			// 不存在
			Lock lock = new RedisLock(redis, key + "&lock");
			try {
				lock.lockWait();

				if (!redis.exists(key)) {
					long maxId = getMaxId(tableClass, fieldName);
					return (Long) redis.eval(INCR_SCRIPT, Arrays.asList(key), Arrays.asList(maxId + ""));
				}
			} finally {
				lock.unlock();
			}
		}
		return redis.incr(key);
	}

}
