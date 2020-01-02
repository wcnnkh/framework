package scw.aop;

import java.lang.reflect.Method;

public interface FilterChain {
	Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args) throws Throwable;
}
