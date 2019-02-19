package scw.sql.orm.cache;

import java.nio.charset.Charset;

import scw.redis.Redis;

public class RedisCache implements Cache {
	private final static Charset CHARSET = Charset.forName("UTF-8");
	private final Redis redis;
	private final int exp;

	public RedisCache(Redis redis, int exp) {
		this.redis = redis;
		this.exp = exp;
	}

	public <T> T get(Class<T> type, String key) {
		byte[] data = redis.getAndTouch(key.getBytes(CHARSET), exp);
		return CacheUtils.decode(type, data);
	}

	public void delete(String key) {
		redis.delete(key.getBytes(CHARSET));
	}

	public void add(String key, Object bean) {
		redis.set(key.getBytes(CHARSET), CacheUtils.encode(bean), Redis.NX.getBytes(CHARSET),
				Redis.EX.getBytes(CHARSET), exp);
	}

	public void set(String key, Object bean) {
		redis.set(key.getBytes(CHARSET), CacheUtils.encode(bean), Redis.XX.getBytes(CHARSET),
				Redis.EX.getBytes(CHARSET), exp);
	}

}
