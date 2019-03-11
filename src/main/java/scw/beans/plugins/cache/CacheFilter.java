package scw.beans.plugins.cache;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.memcached.Memcached;
import scw.redis.Redis;

public final class CacheFilter implements BeanFilter {
	private final BeanFilter beanFilter;

	public CacheFilter(Memcached memcached, boolean debug) {
		this.beanFilter = new MemcachedCacheFilter(memcached, debug);
	}

	public CacheFilter(Redis redis, boolean debug) {
		this.beanFilter = new RedisCacheFilter(redis, debug);
	}

	public Object doFilter(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		return beanFilter.doFilter(obj, method, args, proxy, beanFilterChain);
	}

}
