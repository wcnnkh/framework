package scw.beans.plugins.cache;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONArray;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.common.utils.StringUtils;

public abstract class AbstractCacheFilter implements Filter {
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

	protected String getKey(Cache cache, Method method, Object[] args) {
		StringBuilder sb = new StringBuilder(128);
		if (StringUtils.isEmpty(cache.prefix())) {
			sb.append(method.toString());
		} else {
			sb.append(cache.prefix());
		}
		sb.append("#");

		if (cache.keyIndex().length == 0) {// 全部
			sb.append(JSONArray.toJSONString(args));
		} else {
			JSONArray jarr = new JSONArray();
			for (int index : cache.keyIndex()) {
				jarr.add(args[index]);
			}
			sb.append(jarr.toJSONString());
		}
		return sb.toString();
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		Cache cache = method.getAnnotation(Cache.class);
		if (cache == null) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		String key = getKey(cache, method, args);
		if (!timerTagMap.contains(key)) {// 如果本地找不到这个任务
			CacheTimerTask task = new CacheTimerTask(key, invoker, proxy, method, args, filterChain, this, debug);
			if (timerTagMap.put(key, true) == null) {
				// 以前没的过
				long exp = cache.timeUnit().toMillis(cache.exp());
				timer.schedule(task, exp, exp);
			}
		}

		Object rtn = getCache(key, method.getReturnType());
		if (rtn == null) {
			rtn = filterChain.doFilter(invoker, proxy, method, args);
			if (rtn != null) {
				setCache(key, (int) cache.timeUnit().toSeconds(cache.exp()), method.getReturnType(), rtn);
			}
		}
		return rtn;
	}
}
