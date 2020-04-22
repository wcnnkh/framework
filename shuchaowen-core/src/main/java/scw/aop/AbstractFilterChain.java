package scw.aop;

public abstract class AbstractFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractFilterChain(FilterChain chain) {
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
