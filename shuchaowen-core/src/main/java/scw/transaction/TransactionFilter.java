package scw.transaction;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 以aop的方式管理事务
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MAX_VALUE)
public final class TransactionFilter implements Filter{
	private static Logger logger = LoggerUtils.getLogger(TransactionFilter.class);
	private final TransactionDefinition transactionDefinition;

	public TransactionFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private void invokerAfter(Object rtn, MethodInvoker invoker) {
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				TransactionManager.setRollbackOnly();
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", invoker.getMethod());
				}
			}
		}
	}
	
	public Object doFilter(MethodInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getSourceClass(),
				invoker.getMethod());
		if (tx == null && TransactionManager.hasTransaction()) {
			Object rtn = filterChain.doFilter(invoker, args);
			invokerAfter(rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? this.transactionDefinition
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = filterChain.doFilter(invoker, args);
			invokerAfter(v, invoker);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
