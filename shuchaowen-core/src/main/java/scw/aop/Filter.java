package scw.aop;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface Filter {
	Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable;
}
