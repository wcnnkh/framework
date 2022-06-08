package io.basc.framework.context.transaction;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;

/**
 * 以aop的方式管理事务
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public final class TransactionMethodInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory
			.getLogger(TransactionMethodInterceptor.class);
	private TransactionDefinition transactionDefinition;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition == null ? TransactionDefinition.DEFAULT
				: transactionDefinition;
	}

	public void setTransactionDefinition(
			TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	private void invokerAfter(Transaction transaction, Object rtn,
			MethodInvoker invoker) {
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

	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		TransactionManager transactionManager = TransactionUtils.getManager();
		Transactional tx = AnnotationUtils.getAnnotation(Transactional.class,
				invoker.getSourceClass(), invoker.getMethod());
		if (tx == null && transactionManager.hasTransaction()) {
			Object rtn = invoker.invoke(args);
			invokerAfter(transactionManager.getTransaction(), rtn, invoker);
			return rtn;
		}

		TransactionDefinition transactionDefinition = tx == null ? getTransactionDefinition()
				: new AnnotationTransactionDefinition(tx);
		Transaction transaction = transactionManager
				.getTransaction(transactionDefinition);
		Object v;
		try {
			v = invoker.invoke(args);
			invokerAfter(transaction, v, invoker);
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
