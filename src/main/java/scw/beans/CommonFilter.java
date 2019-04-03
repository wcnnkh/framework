package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.InitMethod;
import scw.beans.async.AsyncCompleteFilter;
import scw.beans.tcc.TCCTransactionFilter;
import scw.transaction.TransactionFilter;

/**
 * 只能使用BeanFactory来管理
 * @author shuchaowen
 *
 */
public final class CommonFilter implements Filter {
	@Autowrite
	private BeanFactory beanFactory;
	private Collection<Filter> filter;
	
	protected CommonFilter(){};
	
	@InitMethod
	private void init() {
		filter = new LinkedList<Filter>();
		filter.add(beanFactory.get(TransactionFilter.class));
		filter.add(beanFactory.get(TCCTransactionFilter.class));
		filter.add(beanFactory.get(AsyncCompleteFilter.class));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		FilterChain chain = new DefaultFilterChain(filter);
		return chain.doFilter(invoker, proxy, method, args);
	}

}
