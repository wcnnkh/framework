package scw.transaction;

import java.lang.reflect.Method;

import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements Filter {
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

	private Object defaultTransaction(Invoker invoker, Object proxy, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = result(invoker, proxy, method, args, filterChain);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}

	private Object result(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		Object rtn = filterChain.doFilter(invoker, proxy, method, args);
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				TransactionManager.setRollbackOnly();
			}
		}
		return rtn;
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getAnnotation(Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			return defaultTransaction(invoker, proxy, method, args, filterChain);
		}

		Transaction transaction = TransactionManager
				.getTransaction(new AnnoationTransactionDefinition(clzTx, methodTx));
		Object rtn;
		try {
			rtn = result(invoker, proxy, method, args, filterChain);
			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
		return rtn;
	}
}
