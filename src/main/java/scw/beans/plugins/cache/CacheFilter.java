package scw.beans.plugins.cache;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.memcached.Memcached;
import scw.redis.Redis;

public final class CacheFilter implements Filter {
	private final Filter filter;

	public CacheFilter(Memcached memcached, boolean debug) {
		this.filter = new MemcachedCacheFilter(memcached, debug);
	}

	public CacheFilter(Redis redis, boolean debug) {
		this.filter = new RedisCacheFilter(redis, debug);
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		return filter.filter(invoker, proxy, method, args, filterChain);
	}

}
