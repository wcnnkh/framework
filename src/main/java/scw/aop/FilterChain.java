package scw.aop;

import java.lang.reflect.Method;

public interface FilterChain {
	Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable;
}
