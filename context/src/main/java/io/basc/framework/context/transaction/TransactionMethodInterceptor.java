package io.basc.framework.context.transaction;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.execution.Executable;
import io.basc.framework.execution.ExecutionException;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.transaction.RollbackOnly;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.Elements;

/**
 * 以aop的方式管理事务
 * 
 * @author wcnnkh
 *
 */
@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public final class TransactionMethodInterceptor implements ExecutionInterceptor {
	private static Logger logger = LoggerFactory.getLogger(TransactionMethodInterceptor.class);
	private TransactionDefinition transactionDefinition;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition == null ? TransactionDefinition.DEFAULT : transactionDefinition;
	}

	public void setTransactionDefinition(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private void invokerAfter(Transaction transaction, Object rtn, Executor executor) {
		if (rtn != null && (rtn instanceof RollbackOnly)) {
			RollbackOnly result = (RollbackOnly) rtn;
			if (result.isRollbackOnly()) {
				transaction.setRollbackOnly();
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", executor);
				}
			}
		}
	}

	@Override
	public Object intercept(Executable source, Executor executor, Elements<? extends Object> args)
			throws ExecutionException {
		TransactionManager transactionManager = TransactionUtils.getManager();
		Transactional tx = executor.getTypeDescriptor().getAnnotation(Transactional.class);
		if (tx == null && transactionManager.hasTransaction()) {
			Object rtn = executor.execute(args);
			invokerAfter(transactionManager.getTransaction(), rtn, executor);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? getTransactionDefinition()
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = transactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = executor.execute(args);
			invokerAfter(transaction, v, executor);
			transactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			if (tx == null || tx.rollbackFor().isAssignableFrom(e.getClass())) {
				transactionManager.rollback(transaction);
			} else {
				// 重复的commit会直接调用关闭
				transactionManager.commit(transaction);
			}
			throw e;
		}
	}
}
