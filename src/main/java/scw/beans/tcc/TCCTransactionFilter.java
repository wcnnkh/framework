package scw.beans.tcc;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.annotaion.Autowrite;

public final class TCCTransactionFilter implements Filter {
	@Autowrite
	private BeanFactory beanFactory;
	
	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		Object rtn = chain.doFilter(invoker, proxy, method, args);
		TCCManager.transaction(beanFactory, method.getDeclaringClass(), rtn, method, args);
		return rtn;
	}
}
