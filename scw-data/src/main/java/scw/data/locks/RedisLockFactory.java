package scw.data.locks;

import java.util.concurrent.TimeUnit;

import scw.core.instance.annotation.Configuration;
import scw.data.redis.Redis;
import scw.locks.AbstractLockFactory;
import scw.locks.Lock;
import scw.util.XUtils;

@Configuration(order=Integer.MIN_VALUE + 1)
public final class RedisLockFactory extends AbstractLockFactory {
	private final Redis redis;

	public RedisLockFactory(Redis redis) {
		this.redis = redis;
	}

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		return new RedisLock(redis, name, XUtils.getUUID(), (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
	}

}
