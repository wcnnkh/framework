package scw.aop;

import java.lang.reflect.Method;

public interface Filter {
	Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable;
}
