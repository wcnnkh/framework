package scw.locks;

import java.util.concurrent.TimeUnit;

import scw.core.utils.XUtils;
import scw.data.redis.Redis;

public final class RedisLockFactory extends AbstractLockFactory {
	private final Redis redis;

	public RedisLockFactory(Redis redis) {
		this.redis = redis;
	}

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		return new RedisLock(redis, name, XUtils.getUUID(), (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
	}

}
