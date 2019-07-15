package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.annotation.Autowrite;
import scw.beans.annotation.InitMethod;
import scw.beans.async.AsyncCompleteFilter;
import scw.beans.tcc.TCCTransactionFilter;
import scw.core.aop.DefaultFilterChain;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
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
		filter.add(beanFactory.getInstance(TransactionFilter.class));
		filter.add(beanFactory.getInstance(TCCTransactionFilter.class));
		filter.add(beanFactory.getInstance(AsyncCompleteFilter.class));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		FilterChain chain = new DefaultFilterChain(filter);
		return chain.doFilter(invoker, proxy, method, args);
	}

}
