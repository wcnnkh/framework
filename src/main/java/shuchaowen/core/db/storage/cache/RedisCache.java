package shuchaowen.core.db.storage.cache;

import java.io.UnsupportedEncodingException;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class RedisCache implements Cache {
	private final Redis redis;

	public RedisCache(Redis redis) {
		this.redis = redis;
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		byte[] keyByte;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		
		byte[] data = redis.get(keyByte);
		if (data != null) {
			if (exp > 0) {
				redis.expire(keyByte, exp);
			}
		}

		if (data == null || data.length == 0) {
			return null;
		}
		
		return CacheUtils.decode(type, data);
	}

	public void set(String key, int exp, Object data) {
		byte[] keyByte;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}

		byte[] dataByte = CacheUtils.encode(data);
		if (dataByte == null) {
			return;
		}

		if (exp > 0) {
			redis.setex(keyByte, exp, dataByte);
		} else {
			redis.set(keyByte, dataByte);
		}
	}

	public void add(String key, int exp, Object data) {
		byte[] keyByte;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}

		byte[] dataByte = CacheUtils.encode(data);
		if (dataByte == null) {
			return;
		}

		if (exp > 0) {
			redis.setex(keyByte, exp, dataByte);
		} else {
			redis.set(keyByte, dataByte);
		}
	}

	public void delete(String ...key) {
		byte[][] keys = new byte[key.length][];
		try {
			for(int i=0; i<key.length; i++){
				keys[i] = key[i].getBytes("UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		
		redis.delete(keys);
	}
}
