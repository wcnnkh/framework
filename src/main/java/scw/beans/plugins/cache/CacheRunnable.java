package scw.beans.plugins.cache;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilterChain;

final class CacheRunnable implements Runnable {
	private String key;
	private Object obj;
	private Method method;
	private Object[] args;
	private MethodProxy proxy;
	private BeanFilterChain beanFilterChain;
	private AbstractCacheFilter abstractCacheFilter;

	public CacheRunnable(String key, Object obj, Method method, Object[] args, MethodProxy proxy,
			BeanFilterChain beanFilterChain, AbstractCacheFilter abstractCacheFilter) {
		this.key = key;
		this.obj = obj;
		this.method = method;
		this.args = args;
		this.proxy = proxy;
		this.beanFilterChain = beanFilterChain;
		this.abstractCacheFilter = abstractCacheFilter;
	}

	public void run() {
		try {
			Object rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			if (rtn == null) {
				Cache cache = method.getAnnotation(Cache.class);
				abstractCacheFilter.setCache(key, cache.exp(), method.getReturnType(), rtn);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
