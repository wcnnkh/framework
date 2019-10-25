package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.core.utils.StringUtils;
import scw.data.redis.Redis;
import scw.data.redis.enums.EXPX;
import scw.data.redis.enums.NXXX;

public class RedisCountLimit extends AbstractCountLimit {
	private final Redis redis;

	public RedisCountLimit(CountLimitConfig countLimitConfig, Redis redis) {
		super(countLimitConfig);
		this.redis = redis;
	}

	public boolean incr() {
		redis.getStringOperations().set(getCountLimitConfig().getName(), "0", NXXX.NX, EXPX.EX,
				getCountLimitConfig().getPeriod(TimeUnit.SECONDS));
		return redis.getStringOperations().incr(getCountLimitConfig().getName()) <= getCountLimitConfig().getMaxCount();
	}

	public long getCount() {
		String value = redis.getStringOperations().get(getCountLimitConfig().getName());
		return StringUtils.parseLong(value);
	}

	public void reset() {
		redis.getStringOperations().setex(getCountLimitConfig().getName(),
				(int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS), "0");
	}
}
