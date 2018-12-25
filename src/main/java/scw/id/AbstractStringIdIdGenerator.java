package scw.id;

import scw.memcached.Memcached;
import scw.memcached.MemcachedIdGenerator;
import scw.redis.Redis;
import scw.redis.RedisIdGernerator;

public abstract class AbstractStringIdIdGenerator<T> implements IdGenerator<T>{
	protected final IdGenerator<Long> idGenerator;

	public AbstractStringIdIdGenerator(Memcached memcached) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName(), 0);
	}

	public AbstractStringIdIdGenerator(Redis redis) {
		this.idGenerator = new RedisIdGernerator(redis, this.getClass().getName(), 0);
	}
}
