package scw.utils.id;

import scw.utils.memcached.Memcached;

public final class MemcachedIntegerIdGenerator implements IdGenerator<Integer>{
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
