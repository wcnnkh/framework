package shuchaowen.core.db.id;

import shuchaowen.core.util.id.IdGenerator;
import shuchaowen.memcached.Memcached;

public class MemcachedIntegerIdGenerator implements IdGenerator<Integer>{
	private final Memcached memcached;
	private final String key;
	
	public MemcachedIntegerIdGenerator(Memcached memcached, String key, int initId){
		this.key = key;
		this.memcached = memcached;
		memcached.add(key, initId + "");
	}
	
	public Integer next() {
		return (int)memcached.incr(key, 1);
	}
}
