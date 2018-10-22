package shuchaowen.core.util;

import java.nio.charset.Charset;

import shuchaowen.core.cache.Redis;

public class RedisQueue {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final String QUEUE_READ_LOCK = "_lock";
	private final Redis redis;
	private final byte[] queueKey;
	
	public RedisQueue(String queueKey, Redis redis){
		this.queueKey = queueKey.getBytes(DEFAULT_CHARSET);
		this.redis = redis;
	}
	
	public void write(byte[] data){
		redis.lpush(queueKey, data);
	}
	
	public byte[] read(){
		return redis.lpop(queueKey);
	}
	
	public byte[] lockRead(){
		RedisLock redisLock = new RedisLock(redis, queueKey + QUEUE_READ_LOCK);
		if(redisLock.lock()){
			try {
				return read();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				redisLock.unLock();
			}
		}
		return null;
	}

	public Redis getRedis() {
		return redis;
	}

	public byte[] getQueueKey() {
		return queueKey;
	}
}
