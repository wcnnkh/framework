package scw.sql.orm.plugin;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

public class SelectCacheFilter implements BeanFilter {
	private final boolean enable;

	public SelectCacheFilter() {
		this(true);
	}

	public SelectCacheFilter(boolean enable) {
		this.enable = enable;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		SelectCache selectCache = method.getDeclaringClass().getAnnotation(SelectCache.class);
		SelectCache m = method.getAnnotation(SelectCache.class);
		if (selectCache == null && m == null) {
			return def(obj, method, args, proxy, beanFilterChain);
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
			return beanFilterChain.doFilter(obj, method, args, proxy);
		} finally {
			SelectCacheUtils.end();
		}

	}

	public Object def(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		if (SelectCacheUtils.isEnable()) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		SelectCacheUtils.begin(enable);
		try {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		} finally {
			SelectCacheUtils.end();
		}
	}
}
