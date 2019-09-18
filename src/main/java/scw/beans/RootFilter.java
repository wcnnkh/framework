package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.aop.CglibInvoker;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.cglib.proxy.MethodInterceptor;
import scw.core.cglib.proxy.MethodProxy;

public final class RootFilter implements Filter, MethodInterceptor {
	private BeanFactory beanFactory;
	private Filter lastFilter;
	private Collection<String> filterNames;

	public RootFilter(BeanFactory beanFactory, Collection<String> filterNames, Filter lastFilter) {
		this.beanFactory = beanFactory;
		this.lastFilter = lastFilter;
		this.filterNames = filterNames;
	}

	private LinkedList<String> getBeanFilterNameList(Class<?> clz, Method method) {
		// 把重复的filter过渡
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(beanFactory.getRootFilterNames());
		if(filterNames != null){
			list.addAll(filterNames);
		}

		scw.beans.annotation.BeanFilter beanFilter = method.getDeclaringClass()
				.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}
		return list;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (obj instanceof Filter) {
			return proxy.invokeSuper(obj, args);
		}

		Invoker invoker = new CglibInvoker(proxy, obj);
		return invoke(invoker, obj, method, args);
	}

	private Object invoke(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain chain = new BeanFactoryFilterChain(beanFactory,
				getBeanFilterNameList(method.getDeclaringClass(), method), lastFilter);
		return chain.doFilter(invoker, proxy, method, args);
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (proxy instanceof Filter) {
			return invoker.invoke(args);
		}

		return invoke(invoker, proxy, method, args);
	}

}
