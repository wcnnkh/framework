package scw.core.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;

public class EmptyInvocationHandler implements InvocationHandler, Filter{

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		//ingore
		return null;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		//ingore
		return null;
	}
}
