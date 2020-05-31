package scw.aop;


abstract class AbstractIteratorFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractIteratorFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		Filter filter = getNextFilter(invoker, args);
		if (filter == null) {
			return chain == null ? invoker.invoke(args) : chain.doFilter(invoker, args);
		}

		return filter.doFilter(invoker, args, this);
	}

	protected abstract Filter getNextFilter(ProxyInvoker invoker, Object[] args) throws Throwable;
}
