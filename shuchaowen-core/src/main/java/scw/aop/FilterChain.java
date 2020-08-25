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
		if (hasNext()) {
			return iterator.next().doFilter(invoker, args, this);
		} else {
			if (nextFilterChain == null) {
				return invoker.invoke(args);
			} else {
				return nextFilterChain.doFilter(invoker, args);
			}
		}
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
