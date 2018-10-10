package shuchaowen.core.beans;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

public interface BeanFilter {
	Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain) throws Throwable;
}
