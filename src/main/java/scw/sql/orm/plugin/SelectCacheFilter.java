package scw.sql.orm.plugin;

import java.lang.reflect.Method;

import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;

public class SelectCacheFilter implements Filter {
	private final boolean enable;

	public SelectCacheFilter() {
		this(true);
	}

	public SelectCacheFilter(boolean enable) {
		this.enable = enable;
	}

	private Object def(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (SelectCacheUtils.isEnable()) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		SelectCacheUtils.begin(enable);
		try {
			return filterChain.doFilter(invoker, proxy, method, args);
		} finally {
			SelectCacheUtils.end();
		}
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		SelectCache selectCache = method.getDeclaringClass().getAnnotation(SelectCache.class);
		SelectCache m = method.getAnnotation(SelectCache.class);
		if (selectCache == null && m == null) {
			return def(invoker, proxy, method, args, filterChain);
		}

		boolean b = enable;
		if (selectCache != null) {
			b = selectCache.value();
		}

		if (m != null) {
			b = m.value();
		}

		SelectCacheUtils.begin(b);
		try {
			return filterChain.doFilter(invoker, proxy, method, args);
		} finally {
			SelectCacheUtils.end();
		}
	}
}
