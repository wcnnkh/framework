package scw.aop;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;

abstract class AbstractIteratorFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractIteratorFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Invoker invoker, Context context) throws Throwable {
		Filter filter = getNextFilter(invoker, context);
		if (filter == null) {
			return chain == null ? invoker.invoke(context.getArgs()) : chain.doFilter(invoker, context);
		}

		return filter.doFilter(invoker, context, this);
	}

	protected abstract Filter getNextFilter(Invoker invoker, Context context) throws Throwable;
}
