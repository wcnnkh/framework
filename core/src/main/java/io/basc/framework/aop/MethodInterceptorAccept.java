package io.basc.framework.aop;

import io.basc.framework.core.reflect.MethodInvoker;

public interface MethodInterceptorAccept {
	boolean isAccept(MethodInvoker invoker, Object[] args);
}
