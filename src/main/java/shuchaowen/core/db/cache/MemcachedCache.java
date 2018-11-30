package shuchaowen.core.db.cache;

import shuchaowen.core.db.DB;
import shuchaowen.memcached.Memcached;

public class MemcachedCache implements Cache{
	public MemcachedCache(Memcached memcached, DB db, String cacheKey, String sql, Object ...params){
		
	}
	
	public <T> T get() {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

}
