package scw.context.transaction;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Transactional;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.MethodInvoker;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
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
	private final TransactionManager transactionManager;
	private final TransactionDefinition transactionDefinition;

	public TransactionMethodInterceptor() {
		this(TransactionManager.GLOBAL, TransactionDefinition.DEFAULT);
	}

	public TransactionMethodInterceptor(TransactionManager transactionManager, TransactionDefinition transactionDefinition) {
		this.transactionManager = transactionManager;
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
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class, invoker.getSourceClass(),
				invoker.getMethod());
		if (tx == null && transactionManager.hasTransaction()) {
			Object rtn = invoker.invoke(args);
			invokerAfter(transactionManager.getTransaction(), rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? this.transactionDefinition
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
