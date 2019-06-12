package scw.beans;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.core.aop.CglibInvoker;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;

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

		FilterChain chain = new BeanFactoryFilterChain(beanFactory,
				BeanUtils.getBeanFilterNameList(method.getDeclaringClass(), method, filterNames));
		Invoker invoker = new CglibInvoker(proxy, obj);
		return chain.doFilter(invoker, obj, method, args);
	}

}
