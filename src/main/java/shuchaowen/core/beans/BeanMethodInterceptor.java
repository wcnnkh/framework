package shuchaowen.core.beans;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.db.TransactionContext;

public class BeanMethodInterceptor implements MethodInterceptor {
	private final List<BeanFilter> beanFilters;
	private final Class<?> type;
	
	public BeanMethodInterceptor(Class<?> type, List<BeanFilter> beanFilters) {
		this.type = type;
		this.beanFilters = beanFilters;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = BeanUtils.isTransaction(type, method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} catch (Throwable e) {
				throw e;
			} finally {
				TransactionContext.getInstance().commit();
			}
		} else {
			BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}
}
