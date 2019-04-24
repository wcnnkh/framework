package scw.aop;

import java.lang.reflect.Method;

import scw.reflect.Invoker;

public interface FilterChain {
	Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable;
}
