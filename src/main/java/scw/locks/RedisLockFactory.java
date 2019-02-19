package scw.locks;

import scw.common.utils.XUtils;
import scw.redis.Redis;

public final class RedisLockFactory extends AbstractLockFactory {
	private final Redis redis;

	public RedisLockFactory(Redis redis, int default_timeout) {
		super(default_timeout);
		this.redis = redis;
	}

	public Lock getLock(String name, int timeout) {
		return new RedisLock(redis, name, XUtils.getUUID(), timeout);
	}

}
