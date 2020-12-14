package scw.redis.locks;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import scw.core.Constants;
import scw.io.ResourceUtils;
import scw.locks.AbstractLock;
import scw.redis.Redis;
import scw.redis.enums.EXPX;
import scw.redis.enums.NXXX;
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
		boolean b = redis.getStringOperations().set(key, id, NXXX.NX, EXPX.EX, timeout);
		if(b){
			autoRenewal(timeout/2, TimeUnit.SECONDS);
		}
		return b;
	}

	public boolean unlock() {
		AnyValue[] values = redis.getStringOperations().eval(UNLOCK_SCRIPT, Collections.singletonList(key),
				Collections.singletonList(id));
		boolean b = values.length == 0 ? false : values[0].getAsBooleanValue();
		if(b){
			cancelAutoRenewal();
		}
		return b;
	}

	public boolean renewal() {
		return renewal(timeout, TimeUnit.SECONDS);
	}

	public boolean renewal(long time, TimeUnit unit) {
		if(!id.equals(redis.getStringOperations().get(key))){
			return false;
		}
		
		Boolean b = redis.getStringOperations().set(key, id, NXXX.XX, EXPX.EX, (int)unit.toSeconds(time));
		return b == null? false:b;
	}
}
