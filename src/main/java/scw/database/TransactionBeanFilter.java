package scw.database;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.database.annoation.SelectCache;
import scw.database.annoation.Transaction;

/**
 * 事务处理的filter
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements BeanFilter {

	public Object doFilter(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		if (obj instanceof ConnectionSource) {// 数据库连接获取类，不用加上事务
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		TransactionContext.begin();
		Transaction clzTransaction = method.getDeclaringClass().getAnnotation(
				Transaction.class);
		Transaction methodTransaction = method.getAnnotation(Transaction.class);
		if (clzTransaction != null || methodTransaction != null) {
			boolean b = true;
			if (clzTransaction != null) {
				b = clzTransaction.value();
			}

			if (methodTransaction != null) {
				b = clzTransaction.value();
			}
			TransactionContext.getConfig().setAutoCommit(!b);
		}
		try {
			Object value = selectCache(obj, method, args, proxy,
					beanFilterChain);
			TransactionContext.commit();
			return value;
		} finally {
			TransactionContext.end();
		}
	}

	private Object selectCache(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		SelectCache selectCache = method.getDeclaringClass().getAnnotation(
				SelectCache.class);
		SelectCache selectCache2 = method.getAnnotation(SelectCache.class);
		if (selectCache != null || selectCache2 != null) {
			boolean isSelectCache = true;
			if (selectCache != null) {
				isSelectCache = selectCache.value();
			}

			if (selectCache2 != null) {
				isSelectCache = selectCache2.value();
			}

			TransactionContext.getConfig().setSelectCache(isSelectCache);
		}
		return beanFilterChain.doFilter(obj, method, args, proxy);
	}
}
