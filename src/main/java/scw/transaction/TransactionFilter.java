package scw.transaction;

import java.lang.reflect.Method;

import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.reflect.AnnotationUtils;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public final class TransactionFilter implements Filter {
	private final TransactionDefinition transactionDefinition;

	public TransactionFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private Object defaultTransaction(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return result(invoker, proxy, targetClass, method, args, filterChain);
		}

		return transaction(invoker, proxy, targetClass, method, args, filterChain, transactionDefinition);
	}

	private Object transaction(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain, TransactionDefinition transactionDefinition) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = result(invoker, proxy, targetClass, method, args, filterChain);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}

	private Object result(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		Object rtn = filterChain.doFilter(invoker, proxy, targetClass, method, args);
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				TransactionManager.setRollbackOnly();
			}
		}
		return rtn;
	}

	public Object filter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, targetClass, method);
		if (tx == null) {
			return defaultTransaction(invoker, proxy, targetClass, method, args, filterChain);
		}

		return transaction(invoker, proxy, targetClass, method, args, filterChain,
				new AnnotationTransactionDefinition(tx));
	}
}
