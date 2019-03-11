package scw.beans.proxy;

import java.lang.reflect.Method;

public interface FilterChain {
	Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain) throws Throwable;
}
