package scw.rcp.restful;

import java.lang.reflect.Method;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.rcp.Service;

public class RestfulService implements Service{

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
