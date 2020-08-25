package scw.aop;

import java.util.Iterator;

public final class MethodInterceptorChain {
	private final Iterator<? extends MethodInterceptor> iterator;
	private final MethodInterceptorChain nextFilterChain;

	public MethodInterceptorChain(Iterator<? extends MethodInterceptor> iterator) {
		this(iterator, null);
	}

	public MethodInterceptorChain(Iterator<? extends MethodInterceptor> iterator, MethodInterceptorChain nextFilterChain) {
		this.iterator = iterator;
		this.nextFilterChain = nextFilterChain;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		MethodInterceptor filter = getNextFilter(invoker, args);
		if (filter == null) {
			if (nextFilterChain == null) {
				return invoker.invoke(args);
			} else {
				return nextFilterChain.intercept(invoker, args);
			}
		} else {
			return filter.intercept(invoker, args, this);
		}
	}

	private MethodInterceptor getNextFilter(MethodInvoker invoker, Object[] args) {
		if (!hasNext()) {
			return null;
		}

		MethodInterceptor filter = iterator.next();
		if (filter instanceof MethodInterceptorAccept) {
			if (((MethodInterceptorAccept) filter).isAccept(invoker, args)) {
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
