package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;

public final class RootFilter implements Filter {
	private final BeanFactory beanFactory;
	private final Collection<String> filterNames;
	private final Collection<Filter> filters;

	/**
	 * @param beanFactory
	 * @param filterNames
	 * @param filters
	 *            在filerNames之后执行
	 */
	public RootFilter(BeanFactory beanFactory, Collection<String> filterNames, Collection<Filter> filters) {
		this.beanFactory = beanFactory;
		this.filterNames = filterNames;
		this.filters = filters;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (proxy instanceof Filter) {
			return invoker.invoke(args);
		}

		FilterChain chain = new MethodFilterChain(beanFactory, targetClass, method, filterNames, filters);
		return chain.doFilter(invoker, proxy, targetClass, method, args);
	}

}
