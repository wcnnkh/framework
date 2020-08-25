package scw.aop;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface MethodInterceptor {
	Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain chain) throws Throwable;
}
