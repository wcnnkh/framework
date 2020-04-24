package scw.async.filter;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.instance.annotation.Configuration;
import scw.lang.NotSupportedException;

@Configuration(order = Integer.MAX_VALUE)
public final class AsyncFilter implements Filter {
	private final NoArgsInstanceFactory instanceFactory;

	public AsyncFilter(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(Invoker invoker, Context context,
			FilterChain filterChain) throws Throwable {
		Async async = context.getMethod().getAnnotation(Async.class);
		if (async == null) {
			return filterChain.doFilter(invoker, context);
		}

		if (!instanceFactory.isInstance(async.service())) {
			throw new NotSupportedException("not support async: " + context.getMethod());
		}

		instanceFactory.getInstance(async.service()).service(async, context);
		return null;
	}
}