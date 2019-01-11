package scw.beans.plugins.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.alibaba.fastjson.JSONArray;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

public abstract class AbstractCacheFilter implements BeanFilter {
	protected abstract <T> T getCache(String key, Class<T> type) throws Exception;

	protected abstract void setCache(String key, int exp, Class<?> type, Object data) throws Exception;

	protected String getKey(Cache cache, Object obj, Method method, Object[] args){
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
	
	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Cache cache = method.getAnnotation(Cache.class);
		if (cache == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		if (method.getReturnType().isAssignableFrom(Void.class)) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
		
		String cacheKey = getKey(cache, obj, method, args);
		
		
		Object rtn = getCache(cacheKey, method.getReturnType());
		if (rtn == null) {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			setCache(cacheKey, cache.exp(), method.getReturnType(), rtn);
		}
		return rtn;
	}

}
