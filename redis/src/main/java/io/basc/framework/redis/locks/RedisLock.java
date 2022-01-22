package io.basc.framework.redis.locks;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.basc.framework.io.ResourceUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.SetOption;

public final class RedisLock extends RenewableLock {
	private static final String UNLOCK_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/lock.script"), Constants.UTF_8);
	private final Redis redis;
	private final String key;
	private final String id;

	public RedisLock(Redis redis, String key, String id, TimeUnit timeUnit, long timeout) {
		super(timeUnit, timeout);
		this.redis = redis;
		this.key = key;
		this.id = id;
	}

	public boolean tryLock() {
		boolean b = redis.set(key, id, ExpireOption.EX, getTimeout(TimeUnit.SECONDS), SetOption.NX);
		if (b) {
			autoRenewal();
		}
		return b;
	}

	public void unlock() {
		cancelAutoRenewal();
		redis.eval(UNLOCK_SCRIPT, Collections.singletonList(key), Collections.singletonList(id));
		// boolean b = values.length == 0 ? false : values[0].getAsBooleanValue();
	}

	public boolean renewal(long time, TimeUnit unit) {
		if (!id.equals(redis.get(key))) {
			return false;
		}

		Boolean b = redis.set(key, id, ExpireOption.EX, unit.toSeconds(time), SetOption.XX);
		return b == null ? false : b;
	}
}
