package scw.transaction;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorChain;
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
public final class TransactionMethodInterceptor implements MethodInterceptor{
	private static Logger logger = LoggerUtils.getLogger(TransactionMethodInterceptor.class);
	private final TransactionDefinition transactionDefinition;

	public TransactionMethodInterceptor() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionMethodInterceptor(TransactionDefinition transactionDefinition) {
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
	
	public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getSourceClass(),
				invoker.getMethod());
		if (tx == null && TransactionManager.hasTransaction()) {
			Object rtn = filterChain.intercept(invoker, args);
			invokerAfter(rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? this.transactionDefinition
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = filterChain.intercept(invoker, args);
			invokerAfter(v, invoker);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
