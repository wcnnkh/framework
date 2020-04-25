package scw.aop;


abstract class AbstractIteratorFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractIteratorFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Invoker invoker, ProxyContext context) throws Throwable {
		Filter filter = getNextFilter(invoker, context);
		if (filter == null) {
			return chain == null ? invoker.invoke(context.getArgs()) : chain.doFilter(invoker, context);
		}

		return filter.doFilter(invoker, context, this);
	}

	protected abstract Filter getNextFilter(Invoker invoker, ProxyContext context) throws Throwable;
}
