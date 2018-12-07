package shuchaowen.cache;

import java.nio.charset.Charset;

import shuchaowen.db.storage.CacheUtils;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisLock;

public class RedisCacheFactory extends AbstractCacheFactory{
	private final Redis redis;
	private final Charset charset;
	
	public RedisCacheFactory(int exp, Redis redis, boolean lock, String charsetName){
		this(exp, redis, lock, Charset.forName(charsetName));
	}
	
	public RedisCacheFactory(int exp, Redis redis, boolean lock, Charset charset){
		super(exp, lock);
		this.redis = redis;
		this.charset = charset;
	}
	
	public void delete(String key) {
		redis.delete(key.getBytes(charset));
	}
	
	private <T> T getByCache(Class<T> type, String key){
		byte[] data = redis.get(key.getBytes(charset));
		if(data == null){
			return null;
		}
		
		if(getExp() > 0){
			redis.expire(key.getBytes(charset), getExp());
		}
		
		return CacheUtils.decode(type, data);
	}
	
	private void setToCache(String key, Object value){
		if(getExp() > 0){
			redis.setex(key.getBytes(charset), getExp(), CacheUtils.encode(value));
		}else{
			redis.set(key.getBytes(charset), CacheUtils.encode(value));
		}
	}
	
	public <T> T get(String key) {
		CallableInfo<T> callableInfo = getCallable(key);
		if(callableInfo == null){
			return null;
		}
		
		T t = getByCache(callableInfo.getType(), key);
		if(t == null){
			if(isLock()){
				RedisLock lock = new RedisLock(redis, key);
				try {
					lock.lockWait(10);
					t = getByCache(callableInfo.getType(), key);
					if(t == null){
						t = callableInfo.getCallable().call();
						if(t != null){
							setToCache(key, t);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unLock();
				}
			}else{
				try {
					t = callableInfo.getCallable().call();
					if(t != null){
						setToCache(key, t);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return t;
	}
}
