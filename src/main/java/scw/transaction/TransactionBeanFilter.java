package scw.transaction;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements BeanFilter {
	/**
	 * 默认的事务定义
	 */
	private final TransactionDefinition transactionDefinition;

	public TransactionBeanFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionBeanFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getAnnotation(Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			return defaultTransaction(obj, method, args, proxy, beanFilterChain);
		}

		Transaction transaction = TransactionManager
				.getTransaction(new AnnoationTransactionDefinition(clzTx, methodTx));
		Object rtn;
		try {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
		return rtn;
	}

	private Object defaultTransaction(Object obj, Method method, Object[] args, MethodProxy proxy,
			BeanFilterChain beanFilterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = beanFilterChain.doFilter(obj, method, args, proxy);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
