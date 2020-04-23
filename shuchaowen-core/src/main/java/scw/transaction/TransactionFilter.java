package scw.transaction;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
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

	private Object defaultTransaction(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		if (TransactionManager.hasTransaction()) {
			return result(invoker, context, filterChain);
		}

		return transaction(invoker, context, filterChain, transactionDefinition);
	}

	private Object transaction(Invoker invoker, Context context, FilterChain filterChain,
			TransactionDefinition transactionDefinition) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = result(invoker, context, filterChain);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}

	private Object result(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		Object rtn = filterChain.doFilter(invoker, context);
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				TransactionManager.setRollbackOnly();
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", context.getMethod());
				}
			}
		}
		return rtn;
	}

	public Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, context.getTargetClass(),
				context.getMethod());
		if (tx == null) {
			return defaultTransaction(invoker, context, filterChain);
		}

		return transaction(invoker, context, filterChain, new AnnotationTransactionDefinition(tx));
	}
}
