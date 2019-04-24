package scw.transaction;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.reflect.Invoker;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public final class TransactionFilter implements Filter {
	/**
	 * 默认的事务定义
	 */
	private final TransactionDefinition transactionDefinition;

	public TransactionFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private Object defaultTransaction(Invoker invoker, Object proxy, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return result(invoker, proxy, method, args, filterChain);
		}

		return transaction(invoker, proxy, method, args, filterChain, transactionDefinition);
	}

	private Object transaction(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain,
			TransactionDefinition transactionDefinition) throws Throwable {
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

		return transaction(invoker, proxy, method, args, filterChain,
				new AnnotationTransactionDefinition(clzTx, methodTx));
	}
}
