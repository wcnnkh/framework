package io.basc.framework.aop.interceptor;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.reflect.MethodInvoker;
import io.basc.framework.util.ConcurrencyThrottleSupport;

import java.io.Serializable;

public class ConcurrencyThrottleInterceptor extends ConcurrencyThrottleSupport
		implements MethodInterceptor, Serializable {
	private static final long serialVersionUID = 1L;

	public ConcurrencyThrottleInterceptor() {
		setConcurrencyLimit(1);
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		beforeAccess();
		try {
			return invoker.invoke(args);
		} finally {
			afterAccess();
		}
	}
}
