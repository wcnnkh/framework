package scw.utils.id;

import scw.utils.redis.Redis;

public final class RedisIdGenerator implements IdGenerator<Long>{
	private final Redis redis;
	private final String key;
	
	public RedisIdGenerator(Redis redis, String key, long initId){
		this.redis = redis;
		this.key = key;
		redis.setnx(key, initId + "");
	}
	
	public Long next() {
		return redis.incr(key);
	}
}
