package shuchaowen.core.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.redis.Redis;

public class RedisIdGernerator implements IdGenerator<Long>{
	private final Redis redis;
	private final String key;
	
	public RedisIdGernerator(Redis redis, String key, long initId){
		this.redis = redis;
		this.key = key;
		redis.setnx(key, initId + "");
	}
	
	public Long next() {
		return redis.incr(key);
	}
}
