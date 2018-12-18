package scw.memcached;

import scw.common.IdGenerator;

public class MemcachedIdGenerator implements IdGenerator<Long>{
	private final Memcached memcached;
	private final String key;
	
	public MemcachedIdGenerator(Memcached memcached, String key, long initId){
		this.key = key;
		this.memcached = memcached;
		memcached.add(key, initId + "");
	}
	
	public Long next() {
		return memcached.incr(key, 1);
	}
}
