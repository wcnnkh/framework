package scw.redis.locks;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import scw.core.Constants;
import scw.io.ResourceUtils;
import scw.locks.RenewableLock;
import scw.redis.Redis;
import scw.redis.enums.EXPX;
import scw.redis.enums.NXXX;

public final class RedisLock extends RenewableLock {
	private static final String UNLOCK_SCRIPT = ResourceUtils.getContent(ResourceUtils.getSystemResource("/scw/data/redis/lock.script"),
			Constants.UTF_8);
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
		boolean b = redis.getStringOperations().set(key, id, NXXX.NX, EXPX.EX, getTimeout(TimeUnit.SECONDS));
		if(b){
			autoRenewal();
		}
		return b;
	}

	public void unlock() {
		cancelAutoRenewal();
		redis.getStringOperations().eval(UNLOCK_SCRIPT, Collections.singletonList(key),
				Collections.singletonList(id));
		//boolean b = values.length == 0 ? false : values[0].getAsBooleanValue();
	}

	public boolean renewal(long time, TimeUnit unit) {
		if(!id.equals(redis.getStringOperations().get(key))){
			return false;
		}
		
		Boolean b = redis.getStringOperations().set(key, id, NXXX.XX, EXPX.EX, (int)unit.toSeconds(time));
		return b == null? false:b;
	}
}
