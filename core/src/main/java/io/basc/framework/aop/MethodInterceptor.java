package io.basc.framework.aop;

import io.basc.framework.core.reflect.MethodInvoker;

@FunctionalInterface
public interface MethodInterceptor {
	Object intercept(MethodInvoker invoker, Object[] args) throws Throwable;
}
