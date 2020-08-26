package scw.aop;

import java.util.Iterator;

public final class MethodInterceptorChain {
	private final Iterator<? extends MethodInterceptor> iterator;
	private final MethodInterceptorChain next;

	public MethodInterceptorChain(Iterator<? extends MethodInterceptor> iterator) {
		this(iterator, null);
	}

	public MethodInterceptorChain(Iterator<? extends MethodInterceptor> iterator, MethodInterceptorChain next) {
		this.iterator = iterator;
		this.next = next;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		MethodInterceptor interceptor = getNextMethodInterceptor(invoker, args);
		if (interceptor == null) {
			if (next == null) {
				return invoker.invoke(args);
			} else {
				return next.intercept(invoker, args);
			}
		} else {
			return interceptor.intercept(invoker, args, this);
		}
	}

	private MethodInterceptor getNextMethodInterceptor(MethodInvoker invoker, Object[] args) {
		if (!hasNext()) {
			return null;
		}

		MethodInterceptor filter = iterator.next();
		if (filter instanceof MethodInterceptorAccept) {
			if (((MethodInterceptorAccept) filter).isAccept(invoker, args)) {
				return filter;
			} else {
				return getNextMethodInterceptor(invoker, args);
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

		if (next != null && next.hasNext()) {
			return false;
		}
		return true;
	}
}
