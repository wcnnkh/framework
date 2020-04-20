package scw.async.filter;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MAX_VALUE)
public final class AsyncFilter implements Filter {
	private final AsyncService asyncService;

	public AsyncFilter(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass,
			Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		Async async = method.getAnnotation(Async.class);
		if (async == null) {
			return filterChain.doFilter(invoker, proxy, targetClass, method,
					args);
		}

		asyncService.service(async, targetClass, method, args);
		return null;
	}
}