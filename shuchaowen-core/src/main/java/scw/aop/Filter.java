package scw.aop;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface Filter {
	Object doFilter(MethodInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable;
}
