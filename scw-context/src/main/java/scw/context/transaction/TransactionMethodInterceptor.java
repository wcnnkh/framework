package scw.context.transaction;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.MethodInvoker;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionManager;
import scw.transaction.TransactionUtils;

/**
 * 以aop的方式管理事务
 * @author shuchaowen
 *
 */
@Provider(order=Ordered.HIGHEST_PRECEDENCE)
public final class TransactionMethodInterceptor implements MethodInterceptor{
	private static Logger logger = LoggerFactory.getLogger(TransactionMethodInterceptor.class);
	private TransactionDefinition transactionDefinition;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition == null? TransactionDefinition.DEFAULT:transactionDefinition;
	}

	public void setTransactionDefinition(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private void invokerAfter(Transaction transaction, Object rtn, MethodInvoker invoker) {
		if (rtn != null && (rtn instanceof RollbackOnlyResult)) {
			RollbackOnlyResult result = (RollbackOnlyResult) rtn;
			if (result.isRollbackOnly()) {
				transaction.setRollbackOnly(true);
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", invoker.getMethod());
				}
			}
		}
	}
	
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		TransactionManager transactionManager = TransactionUtils.getManager();
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getDeclaringClass(),
				invoker.getMethod());
		if (tx == null && transactionManager.hasTransaction()) {
			Object rtn = invoker.invoke(args);
			invokerAfter(transactionManager.getTransaction(), rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? getTransactionDefinition()
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = transactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = invoker.invoke(args);
			invokerAfter(transaction, v, invoker);
			transactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			transactionManager.rollback(transaction);
			throw e;
		}
	}
}
