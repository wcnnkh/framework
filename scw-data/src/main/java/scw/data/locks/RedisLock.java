package scw.data.locks;

import java.util.Collections;

import scw.core.Constants;
import scw.data.redis.Redis;
import scw.data.redis.enums.EXPX;
import scw.data.redis.enums.NXXX;
import scw.io.ResourceUtils;
import scw.locks.AbstractLock;
import scw.value.AnyValue;

public final class RedisLock extends AbstractLock {
	private static final String UNLOCK_SCRIPT = ResourceUtils.getContent("classpath:/scw/data/redis/lock.script",
			Constants.UTF_8);
	private final Redis redis;
	private final String key;
	private final int timeout;
	private final String id;

	public RedisLock(Redis redis, String key, String id, int timeout) {
		this.redis = redis;
		this.key = key;
		this.timeout = timeout;
		this.id = id;
	}

	public boolean tryLock() {
		return redis.getStringOperations().set(key, id, NXXX.NX, EXPX.EX, timeout);
	}

	public boolean unlock() {
		AnyValue[] values = redis.getStringOperations().eval(UNLOCK_SCRIPT, Collections.singletonList(key),
				Collections.singletonList(id));
		return values.length == 0 ? false : values[0].getAsBooleanValue();
	}
}
