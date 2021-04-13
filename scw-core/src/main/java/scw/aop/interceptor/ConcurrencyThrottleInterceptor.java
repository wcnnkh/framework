package scw.aop.interceptor;

import java.io.Serializable;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;
import scw.util.ConcurrencyThrottleSupport;

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
