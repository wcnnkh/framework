package scw.aop;

import scw.core.reflect.MethodInvoker;

public interface MethodInterceptorAccept {
	boolean isAccept(MethodInvoker invoker, Object[] args);
}
