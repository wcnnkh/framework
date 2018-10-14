package shuchaowen.core.db.storage.cache;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.storage.CacheUtils;

public class MemcachedBinaryCache implements Cache{
	private final Memcached memcached;
	
	public MemcachedBinaryCache(Memcached memcached){
		this.memcached = memcached;
	}
	
	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		byte[] v = memcached.getAndTocuh(key, exp);
		if(v == null){
			return null;
		}
		
		return CacheUtils.decode(type, v);
	}

	public void set(String key, int exp, Object data) {
		byte[] v = CacheUtils.encode(data);
		if(v == null){
			return ;
		}
		memcached.set(key, exp, v);
	}

	public void add(String key, int exp, Object data) {
		byte[] v = CacheUtils.encode(data);
		if(v == null){
			return ;
		}
		memcached.add(key, exp, v);
	}

	public void delete(String ...key) {
		for(String k : key){
			memcached.delete(k);
		}
	}
}
