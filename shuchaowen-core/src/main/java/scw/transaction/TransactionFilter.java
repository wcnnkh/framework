package scw.transaction;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.annotation.Configuration;
import scw.core.annotation.AnnotationUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
@Configuration
public final class TransactionFilter implements Filter {
	private static Logger logger = LoggerUtils.getLogger(TransactionFilter.class);
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
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", method);
				}
			}
		}
		return rtn;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, targetClass, method);
		if (tx == null) {
			return defaultTransaction(invoker, proxy, targetClass, method, args, filterChain);
		}

		return transaction(invoker, proxy, targetClass, method, args, filterChain,
				new AnnotationTransactionDefinition(tx));
	}
}
