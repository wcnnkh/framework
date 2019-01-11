package scw.beans.plugins.cache;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class CacheFilter implements BeanFilter {
	private final BeanFilter beanFilter;

	public CacheFilter(Memcached memcached) {
		this.beanFilter = new MemcachedCacheFilter(memcached);
	}

	public CacheFilter(Redis redis) {
		this.beanFilter = new RedisCacheFilter(redis);
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		return beanFilter.doFilter(obj, method, args, proxy, beanFilterChain);
	}

}
