package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.core.reflect.MethodInvokerWrapper;

import java.util.Iterator;

public class MethodInterceptorsInvoker extends MethodInvokerWrapper implements MethodInterceptor {
	private static final long serialVersionUID = 1L;
	private final Iterator<MethodInterceptor> iterator;

	public MethodInterceptorsInvoker(MethodInvoker source, Iterator<MethodInterceptor> iterator) {
		super(source);
		this.iterator = iterator;
	}

	private MethodInterceptor getNextMethodInterceptor(MethodInvoker invoker, Object[] args) {
		if (iterator.hasNext()) {
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
		return null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		MethodInterceptor interceptor = getNextMethodInterceptor(invoker, args);
		if (interceptor == null) {
			return invoker.invoke(args);
		}
		MethodInvoker nextInvoker = new MethodInterceptorsInvoker(invoker, iterator);
		return interceptor.intercept(nextInvoker, args);
	}

	@Override
	public Object invoke(Object... args) throws Throwable {
		return intercept(getSource(), args);
	}
}
