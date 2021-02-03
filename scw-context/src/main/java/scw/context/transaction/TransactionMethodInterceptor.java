package scw.context.transaction;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Transactional;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.MethodInvoker;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionManager;

/**
 * 以aop的方式管理事务
 * @author shuchaowen
 *
 */
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
	
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getSourceClass(),
				invoker.getMethod());
		if (tx == null && TransactionManager.hasTransaction()) {
			Object rtn = invoker.invoke(args);
			invokerAfter(rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? this.transactionDefinition
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = TransactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = invoker.invoke(args);
			invokerAfter(v, invoker);
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
