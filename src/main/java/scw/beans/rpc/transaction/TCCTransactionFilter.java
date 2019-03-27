package scw.beans.rpc.transaction;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.BeanFactory;

final class TCCTransactionFilter implements Filter {
	
	private final Object obj;
	private final Class<?> interfaceClass;
	private final BeanFactory beanFactory;
	
	public TCCTransactionFilter(BeanFactory beanFactory, Class<?> interfaceClass, Object obj) {
		this.beanFactory = beanFactory;
		this.obj = obj;
		this.interfaceClass = interfaceClass;
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		Object rtn = method.invoke(obj, args);
		TCCManager.transaction(beanFactory, interfaceClass, rtn, obj, method, args);
		return rtn;
	}
}
