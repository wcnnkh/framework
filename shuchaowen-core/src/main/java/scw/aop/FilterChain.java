package scw.aop;

import java.util.Iterator;

public final class FilterChain {
	private final Iterator<? extends Filter> iterator;
	private final FilterChain nextFilterChain;

	public FilterChain(Iterator<? extends Filter> iterator) {
		this(iterator, null);
	}

	public FilterChain(Iterator<? extends Filter> iterator, FilterChain nextFilterChain) {
		this.iterator = iterator;
		this.nextFilterChain = nextFilterChain;
	}

	public Object doFilter(MethodInvoker invoker, Object[] args) throws Throwable {
		Filter filter = getNextFilter(invoker, args);
		if (filter == null) {
			if (nextFilterChain == null) {
				return invoker.invoke(args);
			} else {
				return nextFilterChain.doFilter(invoker, args);
			}
		} else {
			return filter.doFilter(invoker, args, this);
		}
	}

	private Filter getNextFilter(MethodInvoker invoker, Object[] args) {
		if (!hasNext()) {
			return null;
		}

		Filter filter = iterator.next();
		if (filter instanceof FilterAccept) {
			if (((FilterAccept) filter).isAccept(invoker, args)) {
				return filter;
			} else {
				return getNextFilter(invoker, args);
			}
		}
		return filter;
	}

	private boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}

	/**
	 * 是否是最后一个filter
	 * 
	 * @return
	 */
	public boolean isLast() {
		if (hasNext()) {
			return false;
		}

		if (nextFilterChain != null && nextFilterChain.hasNext()) {
			return false;
		}
		return true;
	}
}
