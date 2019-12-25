package scw.aop;

import java.lang.reflect.Method;

public abstract class AbstractFilterChain implements FilterChain{
	private final FilterChain chain;

	public AbstractFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		Filter filter = getNextFilter(invoker, proxy, targetClass, method, args);
		if (filter == null) {
			return chain == null ? invoker.invoke(args) : chain.doFilter(invoker, proxy, targetClass, method, args);
		}

		return filter.doFilter(invoker, proxy, targetClass, method, args, this);
	}

	protected abstract Filter getNextFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method,
			Object[] args) throws Throwable;
}
