package io.basc.framework.aop;

import io.basc.framework.reflect.MethodInvoker;

public interface MethodInterceptorAccept {
	boolean isAccept(MethodInvoker invoker, Object[] args);
}
