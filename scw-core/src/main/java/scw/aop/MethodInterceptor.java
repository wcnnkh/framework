package scw.aop;

import scw.core.reflect.MethodInvoker;

@FunctionalInterface
public interface MethodInterceptor {
	Object intercept(MethodInvoker invoker, Object[] args) throws Throwable;
}
