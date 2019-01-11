package scw.beans.plugins.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

import com.alibaba.fastjson.JSONArray;

public abstract class AbstractCacheFilter implements BeanFilter {
	private final ConcurrentHashMap<String, CacheTimerTask> invokeMap = new ConcurrentHashMap<String, CacheTimerTask>();
	/**
	 * 使用timer是原因是允许任务存在延迟，因为这是可以接受的,并且也可以节约服务器资源
	 */
	private final Timer timer = new Timer(this.getClass().getName(), true);// 守护进程，服务器关闭也就停止了

	protected abstract <T> T getCache(String key, Class<T> type)
			throws Exception;

	protected abstract void setCache(String key, int exp, Class<?> type,
			Object data) throws Exception;

	protected String getKey(Cache cache, Object obj, Method method,
			Object[] args) {
		StringBuilder sb = new StringBuilder(512);
		sb.append(this.getClass().getName());
		sb.append("#");

		Parameter[] parameters = method.getParameters();
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				sb.append(parameter.getType().getName());
				sb.append("#");
			}
		}

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

	public Object doFilter(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Cache cache = method.getAnnotation(Cache.class);
		if (cache == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		if (cache.exp() == 0 || method.getReturnType().isAssignableFrom(Void.class)) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		String key = getKey(cache, obj, method, args);
		if (!invokeMap.contains(key)) {// 如果本地找不到这个任务
			CacheTimerTask task = new CacheTimerTask(key, obj, method, args,
					proxy, beanFilterChain, this);
			if (invokeMap.put(key, task) == null) {
				// 以前没的过
				timer.schedule(task, cache.exp(), cache.exp());
			}
		}

		Object rtn = getCache(key, method.getReturnType());
		if (rtn == null) {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			setCache(key, cache.exp(), method.getReturnType(), rtn);
		}
		return rtn;
	}
}
