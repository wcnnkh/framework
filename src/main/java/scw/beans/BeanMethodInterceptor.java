package scw.beans;

import java.lang.reflect.Method;
import java.util.LinkedList;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.cglib.CglibInvoker;

/**
 * 顶层的filter
 * 
 * @author shuchaowen
 *
 */
public final class BeanMethodInterceptor implements MethodInterceptor {
	private String[] filterNames;
	private BeanFactory beanFactory;

	public BeanMethodInterceptor(BeanFactory beanFactory, String[] filterNames) {
		this.filterNames = filterNames;
		this.beanFactory = beanFactory;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (obj instanceof Filter) {
			return proxy.invokeSuper(obj, args);
		}

		// 把重复的filter过渡
		LinkedList<String> list = new LinkedList<String>();
		if (filterNames != null) {
			for (String name : filterNames) {
				list.add(name);
			}
		}

		scw.beans.annotaion.BeanFilter beanFilter = method.getDeclaringClass()
				.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		FilterChain chain = new BeanFactoryFilterChain(beanFactory, list);
		Invoker invoker = new CglibInvoker(proxy, obj);
		return chain.doFilter(invoker, obj, method, args);
	}

}
