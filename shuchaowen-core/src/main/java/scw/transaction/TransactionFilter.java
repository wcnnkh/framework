package scw.transaction;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.ProxyInvoker;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MAX_VALUE)
public final class TransactionFilter implements Filter {
	private static Logger logger = LoggerUtils.getLogger(TransactionFilter.class);
	private final TransactionDefinition transactionDefinition;

	public TransactionFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private Object defaultTransaction(ProxyInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return result(invoker, args, filterChain);
		}

		return transaction(invoker, args, filterChain, transactionDefinition);
	}

	private Object transaction(ProxyInvoker invoker, Object[] args, FilterChain filterChain,
			TransactionDefinition transactionDefinition) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = result(invoker, args, filterChain);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}

	private Object result(ProxyInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		Object rtn = filterChain.doFilter(invoker, args);
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				TransactionManager.setRollbackOnly();
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", invoker.getMethod());
				}
			}
		}
		return rtn;
	}

	public Object doFilter(ProxyInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getTargetClass(),
				invoker.getMethod());
		if (tx == null) {
			return defaultTransaction(invoker, args, filterChain);
		}

		return transaction(invoker, args, filterChain, new AnnotationTransactionDefinition(tx));
	}
}
