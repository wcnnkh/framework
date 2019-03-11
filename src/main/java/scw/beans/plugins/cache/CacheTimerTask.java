package scw.beans.plugins.cache;

import java.lang.reflect.Method;
import java.util.TimerTask;

import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;
import scw.common.Logger;

final class CacheTimerTask extends TimerTask {
	private final String key;
	private final Invoker invoker;
	private final Object proxy;
	private final Method method;
	private final Object[] args;
	private final FilterChain filterChain;
	private final AbstractCacheFilter abstractCacheFilter;
	private final boolean debug;

	public CacheTimerTask(String key, Invoker invoker, Object proxy, Method method, Object[] args,
			FilterChain filterChain, AbstractCacheFilter abstractCacheFilter, boolean debug) {
		this.key = key;
		this.invoker = invoker;
		this.proxy = proxy;
		this.method = method;
		this.args = args;
		this.filterChain = filterChain;
		this.abstractCacheFilter = abstractCacheFilter;
		this.debug = debug;
	}

	@Override
	public void run() {
		if (debug) {
			Logger.debug(this.getClass().getName(), key);
		}

		try {
			Object rtn = filterChain.doFilter(invoker, proxy, method, args, filterChain);
			if (rtn != null) {
				Cache cache = method.getAnnotation(Cache.class);
				abstractCacheFilter.setCache(key, (int) cache.timeUnit().toSeconds(cache.exp()), method.getReturnType(),
						rtn);
			}
		} catch (Throwable e) {
			Logger.error(this.getClass().getName(), key, e);
		}
	}

}
