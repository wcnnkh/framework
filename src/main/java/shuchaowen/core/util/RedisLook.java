package shuchaowen.core.util;

import shuchaowen.core.cache.Redis;

//TODO 未完成
public class RedisLook {
	private final static String NULL_STRING = "";
	private final Redis redis;
	private final String key;
	private final int timeout;
	
	public RedisLook(Redis redis, String key,  int timeout){
		this.redis = redis;
		this.key = key;
		this.timeout = timeout;
	}
	
	public boolean lock(){
		return false;
	}
	
	public void unLock(){
		
	}
}
