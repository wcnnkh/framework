package scw.beans.plugins.cache;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

import com.alibaba.fastjson.JSONArray;

public abstract class AbstractCacheFilter implements BeanFilter {
	private final ConcurrentHashMap<String, Boolean> timerTagMap = new ConcurrentHashMap<String, Boolean>();
	/**
	 * 使用timer是原因是允许任务存在延迟，因为这是可以接受的,并且也可以节约服务器资源
	 */
	private final Timer timer = new Timer(this.getClass().getName(), true);// 守护进程，服务器关闭也就停止了
	private final boolean debug;

	public AbstractCacheFilter(boolean debug) {
		this.debug = debug;
	}

	protected abstract <T> T getCache(String key, Class<T> type) throws Exception;

	protected abstract void setCache(String key, int exp, Class<?> type, Object data) throws Exception;

	protected String getKey(Cache cache, Object obj, Method method, Object[] args) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(method.toString());
		sb.append("#");

		JSONArray jarr = new JSONArray();
		if (cache.keyIndex() != null && cache.keyIndex().length == 0) {// 全部
			for (Object o : args) {
				jarr.add(o);
			}
		} else {
			for (int index : cache.keyIndex()) {
				jarr.add(args[index]);
			}
		}

		sb.append(jarr.toJSONString());
		return sb.toString();
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Cache cache = method.getAnnotation(Cache.class);
		if (cache == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		if (cache.exp() == 0) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		String key = getKey(cache, obj, method, args);
		Object rtn = getCache(key, method.getReturnType());
		if (rtn == null) {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			if (rtn != null) {
				if (!timerTagMap.contains(key)) {// 如果本地找不到这个任务
					CacheTimerTask task = new CacheTimerTask(key, obj, method, args, proxy, beanFilterChain, this,
							debug);
					if (timerTagMap.put(key, true) == null) {
						// 以前没的过
						timer.schedule(task, cache.timeUnit().toMillis(cache.exp()),
								cache.timeUnit().toMillis(cache.exp()));
					}
				}
				setCache(key, (int) cache.timeUnit().toSeconds(cache.exp()), method.getReturnType(), rtn);
			}
		}
		return rtn;
	}
}
